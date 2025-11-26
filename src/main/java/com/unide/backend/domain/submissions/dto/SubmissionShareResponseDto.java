// 제출 코드 공유 상태 변경 응답 DTO

package com.unide.backend.domain.submissions.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionShareResponseDto {
    private Long submissionId;
    private boolean isShared;
    private String message;
}
