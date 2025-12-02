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

    @Transactional
    public HabitAnalysisResponseDto analyzeCodingHabits(User user) {
        List<Submissions> recentSubmissions = submissionsRepository.findTopCorrectSubmissionsByUser(
                user, PageRequest.of(0, 5));

        if (recentSubmissions.isEmpty()) {
            throw new IllegalArgumentException("분석할 정답 코드가 충분하지 않습니다. 문제를 먼저 풀어보세요!");
        }

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
                
                응답 형식:
                {
                  "summary": "전반적인 코딩 스타일 요약 (한글)",
                  "strengths": ["장점1", "장점2", ...],
                  "weaknesses": ["단점/개선점1", "단점/개선점2", ...],
                  "suggestions": ["추천 학습 키워드1", "추천 학습 키워드2", ...]
                }
                
                사용자 코드:
                %s
                """, codeContext.toString());

        String jsonResponse = llmService.getResponse(userPrompt, systemInstruction);

        HabitAnalysisResponseDto result;
        try {
            result = objectMapper.readValue(jsonResponse, HabitAnalysisResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("LLM 응답 파싱 실패: {}", jsonResponse, e);
            throw new RuntimeException("AI 분석 결과를 처리하는 중 오류가 발생했습니다.");
        }

        try {
            UserAnalysisReport report = UserAnalysisReport.builder()
                    .user(user)
                    .summary(result.getSummary())
                    .strengths(objectMapper.writeValueAsString(result.getStrengths()))
                    .weaknesses(objectMapper.writeValueAsString(result.getWeaknesses()))
                    .suggestions(objectMapper.writeValueAsString(result.getSuggestions()))
                    .build();
            
            userAnalysisReportRepository.save(report);
        } catch (JsonProcessingException e) {
            log.error("분석 리포트 저장 실패", e);
        }

        return result;
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
}
