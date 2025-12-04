package com.unide.backend.domain.analysis.service;

import com.unide.backend.common.llm.service.LlmService;
import com.unide.backend.domain.analysis.dto.HabitAnalysisResponseDto;
import com.unide.backend.domain.analysis.entity.UserAnalysisReport;
import com.unide.backend.domain.analysis.repository.UserAnalysisReportRepository;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.analysis.dto.ComplexityAnalysisResponseDto;
import com.unide.backend.domain.analysis.entity.SubmissionComplexity;
import com.unide.backend.domain.analysis.repository.SubmissionComplexityRepository;
import com.unide.backend.domain.analysis.dto.FlowchartResponseDto;
import com.unide.backend.domain.analysis.entity.SubmissionFlowchart;
import com.unide.backend.domain.analysis.repository.SubmissionFlowchartRepository;
import com.unide.backend.domain.mypage.entity.Stats;
import com.unide.backend.domain.mypage.repository.StatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final SubmissionsRepository submissionsRepository;
    private final UserAnalysisReportRepository userAnalysisReportRepository;
    private final LlmService llmService;
    private final ObjectMapper objectMapper;
    private final SubmissionComplexityRepository submissionComplexityRepository;
    private final SubmissionFlowchartRepository submissionFlowchartRepository;
    private final StatsRepository statsRepository;

    @Transactional
    public HabitAnalysisResponseDto analyzeCodingHabits(User user) {
        Stats userStats = statsRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자 통계 정보를 찾을 수 없습니다."));
        int currentSolvedCount = userStats.getTotalSolved();

        Optional<UserAnalysisReport> lastReportOpt = userAnalysisReportRepository.findTopByUserOrderByCreatedAtDesc(user);

        if (lastReportOpt.isPresent()) {
            UserAnalysisReport lastReport = lastReportOpt.get();
            if (currentSolvedCount - lastReport.getAnalyzedSolvedCount() < 10) {
                return convertToDto(lastReport);
            }
        } else {
            if (currentSolvedCount < 5) {
                throw new IllegalArgumentException("분석을 위한 데이터가 부족합니다. 최소 5문제 이상 해결한 뒤 시도해주세요.");
            }
        }

        List<Submissions> recentSubmissions = submissionsRepository.findTopCorrectSubmissionsByUser(
                user, PageRequest.of(0, 5));

        StringBuilder codeContext = new StringBuilder();
        for (int i = 0; i < recentSubmissions.size(); i++) {
            Submissions s = recentSubmissions.get(i);
            codeContext.append(String.format("\n[Code %d - Language: %s]\n%s\n", 
                    i + 1, s.getLanguage(), s.getCode()));
        }

        String systemInstruction = "당신은 전문적인 알고리즘 코딩 코치입니다. 사용자의 코드들을 분석하여 코딩 스타일, 장단점, 개선할 습관을 JSON 형식으로 진단해 주세요.";
        String userPrompt = String.format("""
                다음은 동일한 사용자가 작성한 최근 5개의 알고리즘 풀이 코드입니다.
                이 코드들을 종합적으로 분석하여 다음 항목을 JSON 형식으로 반환해 주세요.
                
                [Response Format]
                {
                  "summary": "전반적인 코딩 스타일 요약 (한글)",
                  "strengths": ["장점1", "장점2"],
                  "weaknesses": ["단점1", "단점2"],
                  "suggestions": ["추천 키워드1", "추천 키워드2"]
                }
                
                [User Codes]
                %s
                """, codeContext.toString());

        String jsonResponse = llmService.getResponse(userPrompt, systemInstruction);
        HabitAnalysisResponseDto resultDto;
        try {
            resultDto = objectMapper.readValue(jsonResponse, HabitAnalysisResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("LLM 응답 파싱 실패", e);
            throw new RuntimeException("AI 분석 결과를 처리하는 중 오류가 발생했습니다.");
        }

        try {
            UserAnalysisReport report = UserAnalysisReport.builder()
                    .user(user)
                    .summary(resultDto.getSummary())
                    .strengths(objectMapper.writeValueAsString(resultDto.getStrengths()))
                    .weaknesses(objectMapper.writeValueAsString(resultDto.getWeaknesses()))
                    .suggestions(objectMapper.writeValueAsString(resultDto.getSuggestions()))
                    .analyzedSolvedCount(currentSolvedCount)
                    .build();
            
            userAnalysisReportRepository.save(report);
        } catch (JsonProcessingException e) {
            log.error("분석 리포트 저장 실패", e);
        }

        return resultDto;
    }

    private HabitAnalysisResponseDto convertToDto(UserAnalysisReport report) {
        try {
            return HabitAnalysisResponseDto.builder()
                    .summary(report.getSummary())
                    .strengths(objectMapper.readValue(report.getStrengths(), List.class))
                    .weaknesses(objectMapper.readValue(report.getWeaknesses(), List.class))
                    .suggestions(objectMapper.readValue(report.getSuggestions(), List.class))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("저장된 분석 데이터를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public ComplexityAnalysisResponseDto analyzeComplexity(Long submissionId, User user) {
        Submissions submission = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출 기록입니다: " + submissionId));

        if (!submission.getUser().getId().equals(user.getId()) && !submission.isShared()) {
            throw new IllegalArgumentException("해당 코드의 분석을 조회할 권한이 없습니다.");
        }

        Optional<SubmissionComplexity> existingAnalysis = submissionComplexityRepository.findBySubmission(submission);
        if (existingAnalysis.isPresent()) {
            SubmissionComplexity result = existingAnalysis.get();
            return ComplexityAnalysisResponseDto.builder()
                    .timeComplexity(result.getTimeComplexity())
                    .timeReason(result.getTimeReason())
                    .spaceComplexity(result.getSpaceComplexity())
                    .spaceReason(result.getSpaceReason())
                    .build();
        }

        String systemInstruction = "당신은 알고리즘 복잡도 분석 전문가입니다. 주어진 코드의 시간 복잡도와 공간 복잡도를 Big-O 표기법으로 추정하고, 그 이유를 간결하게 설명해 주세요. 결과는 반드시 JSON 형식이어야 합니다.";
        String userPrompt = String.format("""
                다음 코드의 시간 복잡도와 공간 복잡도를 분석해 주세요.
                
                [Source Code]
                Language: %s
                Code:
                %s
                
                [Response Format]
                {
                  "timeComplexity": "O(N) 등 Big-O 표기",
                  "timeReason": "시간 복잡도 추정 근거 (1-2문장)",
                  "spaceComplexity": "O(1) 등 Big-O 표기",
                  "spaceReason": "공간 복잡도 추정 근거 (1-2문장)"
                }
                """, submission.getLanguage(), submission.getCode());

        String jsonResponse = llmService.getResponse(userPrompt, systemInstruction);

        ComplexityAnalysisResponseDto resultDto;
        try {
            resultDto = objectMapper.readValue(jsonResponse, ComplexityAnalysisResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("LLM 복잡도 분석 응답 파싱 실패", e);
            throw new RuntimeException("AI 분석 결과를 처리하는 중 오류가 발생했습니다.");
        }

        SubmissionComplexity entity = SubmissionComplexity.builder()
                .submission(submission)
                .timeComplexity(resultDto.getTimeComplexity())
                .timeReason(resultDto.getTimeReason())
                .spaceComplexity(resultDto.getSpaceComplexity())
                .spaceReason(resultDto.getSpaceReason())
                .build();
        
        submissionComplexityRepository.save(entity);

        return resultDto;
    }

    @Transactional
    public FlowchartResponseDto generateFlowchart(Long submissionId, User user) {
        Submissions submission = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출 기록입니다: " + submissionId));

        if (!submission.getUser().getId().equals(user.getId()) && !submission.isShared()) {
            throw new IllegalArgumentException("해당 코드의 플로우 차트를 조회할 권한이 없습니다.");
        }

        Optional<SubmissionFlowchart> existingFlowchart = submissionFlowchartRepository.findBySubmission(submission);
        if (existingFlowchart.isPresent()) {
            return FlowchartResponseDto.builder()
                    .mermaidCode(existingFlowchart.get().getMermaidCode())
                    .build();
        }

        String systemInstruction = "당신은 코드를 시각화하는 전문가입니다. 주어진 코드를 Mermaid.js의 flowchart TD 문법으로 변환해 주세요. 복잡한 세부 구현보다는 핵심 로직(조건문, 반복문)의 흐름을 시각화하는 데 집중하세요. 결과는 반드시 JSON 형식이어야 합니다.";
        String userPrompt = String.format("""
                다음 코드를 Mermaid.js flowchart TD 코드로 변환해 주세요.
                마크다운 태그(```json) 없이 순수 JSON 데이터만 반환해 주세요.
                
                [Source Code]
                Language: %s
                Code:
                %s
                
                [Response Format]
                {
                  "mermaidCode": "graph TD; A[Start] --> B{Condition}; ..."
                }
                """, submission.getLanguage(), submission.getCode());

        String jsonResponse = llmService.getResponse(userPrompt, systemInstruction);

        FlowchartResponseDto resultDto;
        try {
            resultDto = objectMapper.readValue(jsonResponse, FlowchartResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("LLM 플로우 차트 응답 파싱 실패: {}", jsonResponse, e);
            throw new RuntimeException("플로우 차트를 생성하는 중 오류가 발생했습니다.");
        }

        SubmissionFlowchart entity = SubmissionFlowchart.builder()
                .submission(submission)
                .mermaidCode(resultDto.getMermaidCode())
                .build();
        
        submissionFlowchartRepository.save(entity);

        return resultDto;
    }
}
