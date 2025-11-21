// 코드 임시 저장 응답 DTO

package com.unide.backend.domain.submissions.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeDraftSaveResponseDto {
    private String message;
    private Long draftSubmissionId;
}