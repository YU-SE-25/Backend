// 공유된 풀이 목록의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionSolutionDto {
    private Long submissionId;
    private Long userId;
    private String nickname; // 작성자 닉네임
    private SubmissionLanguage language;
    private SubmissionStatus status;
    private Integer runtime;
    private Integer memory;
    private LocalDateTime submittedAt;
}
