// 코드 임시 저장 요청 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeDraftSaveRequestDto {

    @NotNull(message = "문제 ID는 필수입니다.")
    private Long problemId;

    private String code;

    @NotNull(message = "언어 설정은 필수입니다.")
    private SubmissionLanguage language;
}
