// 코드 제출 및 실행 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.submissions.service;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveRequestDto;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveResponseDto;
import com.unide.backend.domain.submissions.dto.CodeDraftResponseDto;
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
}