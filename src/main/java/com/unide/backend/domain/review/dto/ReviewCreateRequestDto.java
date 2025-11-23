// 리뷰 작성 시 클라이언트의 요청을 담는 DTO

package com.unide.backend.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequestDto {
    @NotNull(message = "제출 ID는 필수 입력 값입니다.")
    private Long submissionId;

    @NotBlank(message = "리뷰 내용은 필수 입력 값입니다.")
    private String content;
}
