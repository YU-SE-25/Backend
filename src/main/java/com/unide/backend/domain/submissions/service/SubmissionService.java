// 코드 제출 및 실행 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.submissions.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.efficiency.dto.SubmissionEfficiencyDto;
import com.unide.backend.domain.efficiency.service.EfficiencyService;
import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.entity.TestCase;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.problems.repository.TestCaseRepository;
import com.unide.backend.domain.submissions.dto.CodeDraftResponseDto;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveRequestDto;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveResponseDto;
import com.unide.backend.domain.submissions.dto.CodeRunRequestDto;
import com.unide.backend.domain.submissions.dto.CodeRunResponseDto;
import com.unide.backend.domain.submissions.dto.LongestTimeResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionDetailResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionHistoryDto;
import com.unide.backend.domain.submissions.dto.SubmissionHistoryListDto;
import com.unide.backend.domain.submissions.dto.SubmissionRequestDto;
import com.unide.backend.domain.submissions.dto.SubmissionResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionShareRequestDto;
import com.unide.backend.domain.submissions.dto.SubmissionShareResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionSolutionDto;
import com.unide.backend.domain.submissions.dto.SubmissionSolutionListDto;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.submissions.entity.SubmissionRecord;
import com.unide.backend.domain.submissions.repository.SubmissionRecordRepository;
import com.unide.backend.domain.submissions.dto.SubmissionRecordDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {
    private final SubmissionsRepository submissionsRepository;
    private final ProblemsRepository problemsRepository;
    private final TestCaseRepository testCaseRepository;
    private final DockerService dockerService;
    private final EfficiencyService efficiencyService;
    private final StatsService statsService;
    private final SubmissionRecordRepository submissionRecordRepository;
    
    @Transactional
    public CodeDraftSaveResponseDto saveCodeDraft(User user, CodeDraftSaveRequestDto requestDto) {
        Problems problem = problemsRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제 ID입니다: " + requestDto.getProblemId()));

        Submissions draftSubmission = submissionsRepository
                .findByUserAndProblemAndStatus(user, problem, SubmissionStatus.DRAFT)
                .orElse(null);

        if (draftSubmission != null) {
            draftSubmission.updateDraft(requestDto.getCode(), requestDto.getLanguage());
        } else {
            draftSubmission = Submissions.builder()
                    .user(user)
                    .problem(problem)
                    .code(requestDto.getCode())
                    .language(requestDto.getLanguage())
                    .status(SubmissionStatus.DRAFT)
                    .isShared(false)
                    .totalTestCases(0)
                    .build();
            submissionsRepository.save(draftSubmission);
        }

        return CodeDraftSaveResponseDto.builder()
                .message("임시 저장이 완료되었습니다.")
                .draftSubmissionId(draftSubmission.getId())
                .build();
    }

    public CodeDraftResponseDto getDraftCode(User user, Long problemId) {
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제 ID입니다: " + problemId));

        Submissions draftSubmission = submissionsRepository
                .findByUserAndProblemAndStatus(user, problem, SubmissionStatus.DRAFT)
                .orElseThrow(() -> new IllegalArgumentException("임시 저장된 코드가 없습니다."));

        return CodeDraftResponseDto.builder()
                .code(draftSubmission.getCode())
                .language(draftSubmission.getLanguage())
                .build();
    }

    @Transactional
    public SubmissionResponseDto submitCode(User user, SubmissionRequestDto requestDto) {
        Problems problem = problemsRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제 ID입니다: " + requestDto.getProblemId()));

        List<TestCase> testCases = testCaseRepository.findAllByProblem(problem);
        if (testCases.isEmpty()) {
            throw new IllegalStateException("해당 문제에 대한 테스트 케이스가 존재하지 않습니다.");
        }

        Submissions submission = Submissions.builder()
                .user(user)
                .problem(problem)
                .code(requestDto.getCode())
                .language(requestDto.getLanguage())
                .status(SubmissionStatus.GRADING)
                .isShared(false)
                .totalTestCases(testCases.size())
                .build();
        submissionsRepository.save(submission);

        SubmissionStatus finalStatus = SubmissionStatus.CA;
        long maxRuntime = 0;
        int passedCount = 0;
        String compileOutput = null;
        int index = 1;

        for (TestCase testCase : testCases) {
            CodeRunRequestDto runRequest = new CodeRunRequestDto(
                    requestDto.getCode(), 
                    requestDto.getLanguage(), 
                    testCase.getInput()
            );
            
            CodeRunResponseDto runResult = dockerService.runCode(runRequest);
            
            SubmissionStatus caseStatus = runResult.getStatus(); // 기본적으로 Docker 실행 결과 따름 (CA, TLE, RE, CE)
            int caseRuntime = (int) runResult.getExecutionTimeMs();

            if (caseStatus == SubmissionStatus.CA) {
                String actualOutput = runResult.getOutput().trim();
                String expectedOutput = testCase.getOutput().trim();
                if (!actualOutput.equals(expectedOutput)) {
                    caseStatus = SubmissionStatus.WA;
                }
            }

            SubmissionRecord record = SubmissionRecord.builder()
                    .submission(submission)
                    .testCaseIndex(index++)
                    .status(caseStatus)
                    .runtime(caseRuntime)
                    .memory(0)
                    .build();
            submissionRecordRepository.save(record);

            if (caseStatus == SubmissionStatus.CE) {
                finalStatus = SubmissionStatus.CE;
                compileOutput = runResult.getError();
                passedCount = 0;
                break;
            }

            if (caseStatus != SubmissionStatus.CA) {
                if (finalStatus == SubmissionStatus.CA || finalStatus == SubmissionStatus.WA) {
                    finalStatus = caseStatus;
                } else if (finalStatus == SubmissionStatus.TLE && caseStatus == SubmissionStatus.RE) {
                    finalStatus = SubmissionStatus.RE;
                }
            } else {
                passedCount++;
                maxRuntime = Math.max(maxRuntime, caseRuntime);
            }
        }

        submission.updateResult(
                finalStatus,
                (int) maxRuntime,
                0, 
                passedCount,
                compileOutput
        );
        
        statsService.updateStats(user.getId());       
        statsService.onCodeSubmitted(user.getId());   

        return SubmissionResponseDto.builder()
                .submissionId(submission.getId())
                .status(finalStatus)
                .runtime(submission.getRuntime())
                .memory(submission.getMemory())
                .passedTestCases(submission.getPassedTestCases())
                .totalTestCases(submission.getTotalTestCases())
                .compileOutput(submission.getCompileOutput())
                .message("채점이 완료되었습니다.")
                .build();
    }

    public LongestTimeResponseDto getLongestRuntime(User user, Long problemId) {
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제 ID입니다: " + problemId));

        Integer maxRuntime = submissionsRepository.findMaxRuntimeByUserAndProblemAndStatus(user, problem, SubmissionStatus.CA)
                .orElse(0);

        return LongestTimeResponseDto.builder()
                .longestTimeMs(maxRuntime)
                .build();
    }

    public SubmissionDetailResponseDto getSubmissionDetail(Long submissionId, User user) {
        Submissions submission = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출 기록입니다: " + submissionId));

        if (!submission.getUser().getId().equals(user.getId()) && !submission.isShared()) {
            throw new IllegalArgumentException("해당 제출 기록을 볼 권한이 없습니다.");
        }

        List<SubmissionRecord> records = submissionRecordRepository.findAllBySubmissionOrderByTestCaseIndexAsc(submission);
        List<SubmissionRecordDto> recordDtos = records.stream()
                .map(r -> SubmissionRecordDto.builder()
                        .testCaseIndex(r.getTestCaseIndex())
                        .status(r.getStatus())
                        .runtime(r.getRuntime())
                        .memory(r.getMemory())
                        .build())
                .collect(Collectors.toList());

        return SubmissionDetailResponseDto.builder()
                .submissionId(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .code(submission.getCode())
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .runtime(submission.getRuntime())
                .memory(submission.getMemory())
                .submittedAt(submission.getSubmittedAt())
                .isShared(submission.isShared())
                .records(recordDtos)
                .build();
    }

    @Transactional
    public SubmissionShareResponseDto updateShareStatus(Long submissionId, User user, SubmissionShareRequestDto requestDto) {
        Submissions submission = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출 기록입니다: " + submissionId));

        if (!submission.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 제출 기록만 수정할 수 있습니다.");
        }

        submission.updateShareStatus(requestDto.getIsShared());

        return SubmissionShareResponseDto.builder()
                .submissionId(submission.getId())
                .isShared(submission.isShared())
                .message("공유 상태가 업데이트되었습니다.")
                .build();
    }

    public SubmissionHistoryListDto getSubmissionHistory(User user, Long problemId, Pageable pageable) {
        Page<Submissions> submissionPage;

        if (problemId != null) {
            Problems problem = problemsRepository.findById(problemId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제 ID입니다: " + problemId));
            submissionPage = submissionsRepository.findAllByUserAndProblem(user, problem, pageable);
        } else {
            submissionPage = submissionsRepository.findAllByUser(user, pageable);
        }

        List<SubmissionHistoryDto> historyDtos = submissionPage.getContent().stream()
                .map(submission -> SubmissionHistoryDto.builder()
                        .submissionId(submission.getId())
                        .problemId(submission.getProblem().getId())
                        .problemTitle(submission.getProblem().getTitle())
                        .status(submission.getStatus())
                        .language(submission.getLanguage())
                        .runtime(submission.getRuntime())
                        .memory(submission.getMemory())
                        .submittedAt(submission.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());

        return SubmissionHistoryListDto.builder()
                .totalPages(submissionPage.getTotalPages())
                .totalElements(submissionPage.getTotalElements())
                .currentPage(submissionPage.getNumber())
                .submissions(historyDtos)
                .build();
    }

 public SubmissionSolutionListDto getSharedSolutions(Long problemId, Pageable pageable) {
    if (!problemsRepository.existsById(problemId)) {
        throw new IllegalArgumentException("존재하지 않는 문제 ID입니다: " + problemId);
    }

    // 1) 이 문제의 공유된 제출물 페이지 조회
    Page<Submissions> submissionPage =
            submissionsRepository.findSharedSolutionsByProblem(problemId, pageable);

    List<Submissions> submissions = submissionPage.getContent();

    // 2) 효율 서비스로 문제별 효율 정보 계산
    //    (모든 공유 제출물 대상으로 vote 합계 + rank 구함)
    List<SubmissionEfficiencyDto> efficiencyList =
            efficiencyService.calculateEfficiencyForProblem(problemId);

    // submissionId -> 효율 DTO 맵
    Map<Long, SubmissionEfficiencyDto> efficiencyMap = efficiencyList.stream()
            .collect(Collectors.toMap(
                    SubmissionEfficiencyDto::getSubmissionId,
                    e -> e
            ));

    // 3) Submissions -> SubmissionSolutionDto 매핑 + 효율 정보 주입
    List<SubmissionSolutionDto> solutionDtos = submissions.stream()
            .map(submission -> {
                SubmissionEfficiencyDto eff = efficiencyMap.get(submission.getId());

                Integer totalVotes      = eff != null ? eff.getTotalVotes()      : 0;
                Double efficiencyScore  = eff != null ? eff.getEfficiencyScore() : 0.0;
                Long   efficiencyRank   = eff != null ? eff.getRank()            : null;

                return SubmissionSolutionDto.builder()
                        .submissionId(submission.getId())
                        .userId(submission.getUser().getId())
                        .nickname(submission.getUser().getNickname())
                        .language(submission.getLanguage())
                        .status(submission.getStatus())
                        .runtime(submission.getRuntime())
                        .memory(submission.getMemory())
                        .submittedAt(submission.getSubmittedAt())
                        // === 효율 필드 주입 ===
                        .totalVotes(totalVotes)
                        .efficiencyScore(efficiencyScore)
                        .efficiencyRank(efficiencyRank)
                        .build();
            })
            .collect(Collectors.toList());

    // 4) 페이징 정보 포함해서 리턴
    return SubmissionSolutionListDto.builder()
            .totalPages(submissionPage.getTotalPages())
            .totalElements(submissionPage.getTotalElements())
            .currentPage(submissionPage.getNumber())
            .solutions(solutionDtos)
            .build();
}

}