// 리뷰 투표 결과 응답 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewVoteResponseDto {
    private Long reviewId;
    private int voteCount;
    private boolean viewerLiked;
    private String message;
}
