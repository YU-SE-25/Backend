// 코드 리뷰 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.review.controller;

import com.unide.backend.domain.review.dto.ReviewListResponseDto;
import com.unide.backend.domain.review.service.ReviewService;
import com.unide.backend.domain.review.dto.ReviewCreateRequestDto;
import com.unide.backend.domain.review.dto.ReviewCreateResponseDto;
import com.unide.backend.domain.review.dto.ReviewUpdateRequestDto;
import com.unide.backend.domain.review.dto.ReviewUpdateResponseDto;
import com.unide.backend.domain.review.dto.ReviewDeleteResponseDto;
import com.unide.backend.domain.review.dto.ReviewVoteResponseDto;
import com.unide.backend.domain.review.dto.ReviewCommentListResponseDto;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 특정 제출 코드에 대한 리뷰 목록을 조회하는 API
     * @param submissionId 제출 코드 ID
     * @param pageable 페이징 정보 (기본값: page=0, size=10)
     * @param principalDetails 현재 로그인한 사용자 정보 (옵션)
     * @return 페이징된 리뷰 목록
     */
    @GetMapping("/submissions/{submissionId}/reviews")
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

    /**
     * 새로운 리뷰를 작성하는 API
     * @param principalDetails 현재 로그인한 사용자
     * @param requestDto 리뷰 내용 및 대상 submissionId
     * @return 성공 메시지
    */
    @PostMapping("/reviews")
    public ResponseEntity<ReviewCreateResponseDto> createReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody ReviewCreateRequestDto requestDto) {
        
        ReviewCreateResponseDto response = reviewService.createReview(principalDetails.getUser(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리뷰 내용을 수정하는 API
     * @param reviewId 수정할 리뷰 ID
     * @param principalDetails 현재 로그인한 사용자
     * @param requestDto 수정할 내용
     * @return 수정 결과
    */
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody ReviewUpdateRequestDto requestDto) {
        
        ReviewUpdateResponseDto response = reviewService.updateReview(reviewId, principalDetails.getUser(), requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰를 삭제하는 API
     * @param reviewId 삭제할 리뷰 ID
     * @param principalDetails 현재 로그인한 사용자
     * @return 삭제 결과
    */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDeleteResponseDto> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        ReviewDeleteResponseDto response = reviewService.deleteReview(reviewId, principalDetails.getUser());
        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰에 투표(좋아요)를 하거나 취소하는 API
     * @param reviewId 투표할 리뷰 ID
     * @param principalDetails 현재 로그인한 사용자
     * @return 투표 결과
    */
    @PostMapping("/reviews/{reviewId}/vote")
    public ResponseEntity<ReviewVoteResponseDto> toggleVote(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        ReviewVoteResponseDto response = reviewService.toggleVote(reviewId, principalDetails.getUser());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 리뷰에 대한 댓글 목록을 조회하는 API
     * @param reviewId 리뷰 ID
     * @param pageable 페이징 정보 (기본값: page=0, size=20)
     * @param principalDetails 현재 로그인한 사용자 (옵션)
     * @return 페이징된 댓글 목록
    */
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<ReviewCommentListResponseDto> getReviewComments(
            @PathVariable Long reviewId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        ReviewCommentListResponseDto response = reviewService.getReviewComments(
                reviewId, 
                pageable, 
                principalDetails != null ? principalDetails.getUser() : null
        );
        return ResponseEntity.ok(response);
    }
}
