// 코드 실행 요청 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeRunRequestDto {
    @NotBlank(message = "소스 코드는 필수입니다.")
    private String code;

    @NotNull(message = "언어 설정은 필수입니다.")
    private SubmissionLanguage language;

    private String input;
}
