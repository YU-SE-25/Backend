// 코드 제출 및 실행 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.submissions.service;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.submissions.dto.*;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {
    private final SubmissionsRepository submissionsRepository;
    private final ProblemsRepository problemsRepository;
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

        Submissions submission = Submissions.builder()
                .user(user)
                .problem(problem)
                .code(requestDto.getCode())
                .language(requestDto.getLanguage())
                .status(SubmissionStatus.GRADING)
                .isShared(false)
                .totalTestCases(1) // 임시 값, 실제로는 문제의 테스트 케이스 수로 설정 필요
                .build();
        submissionsRepository.save(submission);

        // (실제로는 문제에 연결된 테스트 케이스를 DB에서 가져와서 반복 실행해야 함. 여기서는 임시 값 사용)
        String testInput = "1 2"; 
        String expectedOutput = "3"; // 예시 정답

        CodeRunRequestDto runRequest = new CodeRunRequestDto(
                requestDto.getCode(), 
                requestDto.getLanguage(), 
                testInput
        );
        
        CodeRunResponseDto runResult = dockerService.runCode(runRequest);

        SubmissionStatus finalStatus;
        String compileOutput = null;

        if (!runResult.isSuccess()) {
            if (runResult.getError().contains("Compilation Error")) {
                finalStatus = SubmissionStatus.CE;
                compileOutput = runResult.getError();
            } else {
                finalStatus = SubmissionStatus.RE;
            }
        } else {
            if (runResult.getOutput().trim().equals(expectedOutput)) {
                finalStatus = SubmissionStatus.CA;
            } else {
                finalStatus = SubmissionStatus.WA;
            }
        }
        
        // (현재 DockerService는 성공/실패만 반환하므로, 필요시 DockerService 로직 고도화 필요. 여기서는 단순 성공/실패로직만 적용)
        submission.updateResult(
                finalStatus,
                (int) runResult.getExecutionTimeMs(),
                0, // 메모리 사용량 (Docker Java API로 정확한 측정은 복잡하므로 일단 0)
                finalStatus == SubmissionStatus.CA ? 1 : 0, // 맞은 테스트 케이스 수
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
