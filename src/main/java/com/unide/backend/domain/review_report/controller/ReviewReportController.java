package com.unide.backend.domain.review_report.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.review_report.dto.ReviewCommentReportCreateRequestDto;
import com.unide.backend.domain.review_report.dto.ReviewReportCreateRequestDto;
import com.unide.backend.domain.review_report.service.ReviewCommentService;
import com.unide.backend.domain.review_report.service.ReviewReportService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewReportController {

    private final ReviewReportService reviewReportService;
    private final ReviewCommentService reviewCommentService;

    /**
     * 코드 리뷰 게시글 신고
     * POST /api/reviews/{reviewId}/reports
     */
    @PostMapping("/{reviewId}/reports")
    public ResponseEntity<Map<String, String>> reportReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody ReviewReportCreateRequestDto dto
    ) {
        Long reporterId = principal.getUser().getId();

        reviewReportService.reportPost(reviewId, reporterId, dto);

        Map<String, String> body = new HashMap<>();
        body.put("message", "신고가 접수되었습니다.");
        return ResponseEntity.ok(body);
    }

    /**
     * 코드 리뷰 댓글 신고
     * POST /api/reviews/{reviewId}/comments/{commentId}/reports
     */
    @PostMapping("/{reviewId}/comments/{commentId}/reports")
    public ResponseEntity<Map<String, String>> reportReviewComment(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody ReviewCommentReportCreateRequestDto dto
    ) {
        Long reporterId = principal.getUser().getId();

        reviewCommentService.reportComment(reviewId, commentId, reporterId, dto);

        Map<String, String> body = new HashMap<>();
        body.put("message", "신고가 접수되었습니다.");
        return ResponseEntity.ok(body);
    }
}
