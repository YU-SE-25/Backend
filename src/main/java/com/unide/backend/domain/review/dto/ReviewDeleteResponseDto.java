// 리뷰 삭제 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewDeleteResponseDto {
    private Long reviewId;
    private String message;
}
