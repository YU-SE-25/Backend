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

import java.util.List;

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

            if (!runResult.isSuccess()) {
                if (runResult.getError() != null && runResult.getError().contains("Compilation Error")) {
                    finalStatus = SubmissionStatus.CE;
                    compileOutput = runResult.getError();
                    break;
                } else {
                    finalStatus = SubmissionStatus.RE;
                    break;
                }
            }

            // 시간 초과 체크 (DockerService가 시간 초과 시 isSuccess=false로 주거나 별도 처리 필요.
            // 현재 구조상 runResult.getExecutionTimeMs()가 timeLimit을 넘으면 TLE 처리 가능)
            String actualOutput = runResult.getOutput().trim();
            String expectedOutput = testCase.getOutput().trim();

            if (actualOutput.equals(expectedOutput)) {
                passedCount++;
                maxRuntime = Math.max(maxRuntime, runResult.getExecutionTimeMs());
            } else {
                finalStatus = SubmissionStatus.WA;
                break; // 하나라도 틀리면 중단 (또는 끝까지 돌리고 틀린 개수만 셀 수도 있음)
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
}
