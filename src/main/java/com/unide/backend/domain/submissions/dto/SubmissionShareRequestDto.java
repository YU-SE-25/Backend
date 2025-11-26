// 제출 코드 공유 상태 변경 요청 DTO

package com.unide.backend.domain.submissions.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubmissionShareRequestDto {

    @NotNull(message = "공유 상태 값은 필수입니다.")
    private Boolean isShared; // true: 공개, false: 비공개
}
