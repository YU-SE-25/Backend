// 리뷰 댓글 목록의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewCommentDto {
    private Long commentId;
    private String commenter;
    private String content;
    private LocalDateTime createdAt;
    private boolean isOwner;
}
