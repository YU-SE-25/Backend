// 리뷰 댓글 작성 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewCommentCreateResponseDto {
    private Long commentId;
    private String message;
    private LocalDateTime createdAt;
}
