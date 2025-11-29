package com.unide.backend.domain.discuss.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.discuss.dto.DiscussCommentReportCreateRequestDto;
import com.unide.backend.domain.discuss.dto.DiscussCommentRequest;
import com.unide.backend.domain.discuss.dto.DiscussCommentResponse;
import com.unide.backend.domain.discuss.dto.DiscussReportCreateRequestDto;
import com.unide.backend.domain.discuss.service.DiscussCommentService;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.discuss.service.DiscussCommentReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dis_board")
public class DiscussCommentController {

    private final DiscussCommentService discussCommentService;
    private final DiscussCommentReportService discussCommentReportService;   // ✅ 신고 서비스 추가

    // ===== 특정 게시글 댓글 목록 조회 =====
    // GET /api/dis_board/{postId}/comments
    @GetMapping("/{postId}/comments")
    public List<DiscussCommentResponse> list(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return discussCommentService.getCommentsByPost(postId, viewerId);
    }

    // ===== 특정 댓글 단건 조회 =====
    // GET /api/dis_board/comment/{commentId}
    @GetMapping("/comment/{commentId}")
    public DiscussCommentResponse detail(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return discussCommentService.getComment(commentId, viewerId);
    }

    // ===== 특정 게시글에 댓글 등록 (대댓글 포함) =====
    // POST /api/dis_board/{postId}/comments
    // body: { "contents": "...", "anonymity": false, "parent_id": 201, "is_private": false }
    @PostMapping("/{postId}/comments")
    public DiscussCommentResponse create(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody DiscussCommentRequest request
    ) {
        Long authorId = userDetails.getUser().getId();
        return discussCommentService.createComment(postId, authorId, request);
    }

    // ===== 댓글 수정 =====
    // PUT /api/dis_board/comment/{commentId}
    @PutMapping("/comment/{commentId}")
    public DiscussCommentResponse update(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody DiscussCommentRequest request
    ) {
        Long authorId = userDetails.getUser().getId();
        return discussCommentService.updateComment(commentId, authorId, request);
    }

    // ===== 댓글 삭제 =====
    // DELETE /api/dis_board/comment/{commentId}
    @DeleteMapping("/comment/{commentId}")
    public Map<String, Object> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long authorId = userDetails.getUser().getId();
        discussCommentService.deleteComment(commentId, authorId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "댓글이 삭제되었습니다.");
        result.put("deleted_id", commentId);
        return result;
    }

    // ===== 특정 댓글에 좋아요 추가(토글) =====
    // POST /api/dis_board/comment/{commentId}/like
    @PostMapping("/comment/{commentId}/like")
    public DiscussCommentResponse like(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return discussCommentService.toggleLike(commentId, userId);
    } // POST /api/dis_board/comment/{commentId}/reports
    @PostMapping("/comment/{commentId}/reports")
    public ResponseEntity<Void> reportComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody DiscussCommentReportCreateRequestDto request   // ✅ 타입 변경
    ) {
        Long reporterId = userDetails.getUser().getId();
        discussCommentReportService.reportPost(commentId, reporterId, request);  // ✅ 이제 시그니처와 일치
        return ResponseEntity.ok().build();
    }
}
