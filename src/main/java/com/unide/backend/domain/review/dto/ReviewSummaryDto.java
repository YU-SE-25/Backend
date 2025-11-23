// 리뷰 목록의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewSummaryDto {
    private Long reviewId;
    private String reviewer;
    private String content;
    private int voteCount;
    private LocalDateTime createdAt;
    private boolean isOwner;
}
