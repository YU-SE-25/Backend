// 리뷰 댓글 목록 전체 응답을 담는 DTO (페이징 정보 포함)

package com.unide.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewCommentListResponseDto {
    private int totalPages;
    private int currentPage;
    private List<ReviewCommentDto> comments;
}
