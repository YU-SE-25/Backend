// 임시 저장된 코드 조회 응답 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeDraftResponseDto {
    private String code;
    private SubmissionLanguage language;
}
