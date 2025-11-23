// 리뷰 수정 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateResponseDto {
    private Long reviewId;
    private String message;
}
