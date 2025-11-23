// 리뷰 작성 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateResponseDto {
    private String message;
}
