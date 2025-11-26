// 코드 제출 및 실행 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.submissions.service;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.entity.TestCase;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.problems.repository.TestCaseRepository;
import com.unide.backend.domain.submissions.dto.*;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {
    private final SubmissionsRepository submissionsRepository;
    private final ProblemsRepository problemsRepository;
    private final TestCaseRepository testCaseRepository;
    private final DockerService dockerService;

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

        for (TestCase testCase : testCases) {
            CodeRunRequestDto runRequest = new CodeRunRequestDto(
                    requestDto.getCode(), 
                    requestDto.getLanguage(), 
                    testCase.getInput()
            );
            
            CodeRunResponseDto runResult = dockerService.runCode(runRequest);

            if (runResult.getStatus() == SubmissionStatus.CE) {
                finalStatus = SubmissionStatus.CE;
                compileOutput = runResult.getError();
                passedCount = 0;
                break;
            }

            if (runResult.getStatus() != SubmissionStatus.CA) {
                // 우선순위에 따라 최종 상태 업데이트 (RE > TLE > WA > CA)
                if (finalStatus == SubmissionStatus.CA || finalStatus == SubmissionStatus.WA) {
                    finalStatus = runResult.getStatus(); 
                } else if (finalStatus == SubmissionStatus.TLE && runResult.getStatus() == SubmissionStatus.RE) {
                    finalStatus = SubmissionStatus.RE;
                }
            } else {
                String actualOutput = runResult.getOutput().trim();
                String expectedOutput = testCase.getOutput().trim();

                if (actualOutput.equals(expectedOutput)) {
                    passedCount++;
                    maxRuntime = Math.max(maxRuntime, runResult.getExecutionTimeMs());
                } else {
                    if (finalStatus == SubmissionStatus.CA) {
                        finalStatus = SubmissionStatus.WA;
                    }
                }
            }
        }

        submission.updateResult(
                finalStatus,
                (int) maxRuntime,
                0, // 메모리 측정은 Java Docker Client로는 복잡하여 0으로 유지 (추후 cgroup 활용 필요)
                passedCount,
                compileOutput
        );

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
}
