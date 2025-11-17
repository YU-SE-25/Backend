// 리뷰 목록 전체 응답을 담는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewListResponseDto {
    private int totalPages;
    private int currentPage;
    private List<ReviewSummaryDto> reviews;
}
