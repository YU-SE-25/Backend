// 코드 리뷰 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.review.controller;

import com.unide.backend.domain.review.dto.ReviewListResponseDto;
import com.unide.backend.domain.review.service.ReviewService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submissions")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 특정 제출 코드에 대한 리뷰 목록을 조회하는 API
     * @param submissionId 제출 코드 ID
     * @param pageable 페이징 정보 (기본값: page=0, size=10)
     * @param principalDetails 현재 로그인한 사용자 정보 (옵션)
     * @return 페이징된 리뷰 목록
     */
    @GetMapping("/{submissionId}/reviews")
    public ResponseEntity<ReviewListResponseDto> getReviewList(
            @PathVariable Long submissionId,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        // 로그인하지 않은 사용자도 리뷰 목록은 볼 수 있으므로 principalDetails가 null일 수 있음
        ReviewListResponseDto response = reviewService.getReviewList(
                submissionId, 
                pageable, 
                principalDetails != null ? principalDetails.getUser() : null
        );
        return ResponseEntity.ok(response);
    }
}
