package com.unide.backend.domain.qna.controller;

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

import com.unide.backend.domain.qna.dto.QnACommentRequest;
import com.unide.backend.domain.qna.dto.QnACommentResponse;
import com.unide.backend.domain.qna.service.QnACommentReportService;
import com.unide.backend.domain.qna.service.QnACommentService;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.discuss.dto.DiscussCommentReportCreateRequestDto;
import com.unide.backend.domain.qna.dto.QnACommentReportCreateRequestDto;
import com.unide.backend.domain.qna.entity.QnA;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor


@RequestMapping("/api/qna_board")
public class QnACommentController {
    private final QnACommentService qnaCommentService;
    private final QnACommentReportService qnACommentReportService;   // ✅ 신고 서비스 추가

     // ===== 특정 게시글 댓글 목록 조회 =====
      @GetMapping("/{postId}/comments")
    public List<QnACommentResponse> list(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return qnaCommentService.getCommentsByPost(postId, viewerId);
    }

      // ===== 특정 댓글 단건 조회 =====
      @GetMapping("/comment/{commentId}")
    public QnACommentResponse detail(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return qnaCommentService.getComment(commentId, viewerId);
    }
    // ===== 특정 게시글에 댓글 등록 (대댓글 포함) =====
     // body: { "contents": "...", "anonymity": false, "parent_id": 201, "is_private": false }
@PostMapping("/{postId}/comments")
    public QnACommentResponse create(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody QnACommentRequest request
    ) {
        Long authorId = userDetails.getUser().getId();
        return qnaCommentService.createComment(postId, authorId, request);
    }
     // ===== 댓글 수정 =====
      @PutMapping("/comment/{commentId}")
    public QnACommentResponse update(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody QnACommentRequest request
    ) {
        Long authorId = userDetails.getUser().getId();
        return qnaCommentService.updateComment(commentId, authorId, request);
    }

    // ===== 댓글 삭제 =====
    @DeleteMapping("/comment/{commentId}")
    public Map<String, Object> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long authorId = userDetails.getUser().getId();
        qnaCommentService.deleteComment(commentId, authorId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "댓글이 삭제되었습니다.");
        result.put("deleted_id", commentId);
        return result;
    }
    // ===== 특정 댓글에 좋아요 추가(토글) =====

    @PostMapping("/comment/{commentId}/like")
    public QnACommentResponse like(
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return qnaCommentService.toggleLike(commentId, userId);
    }
    @PostMapping("/comment/{commentId}/reports")
public ResponseEntity<Map<String, Object>> reportComment(
        @PathVariable("commentId") Long commentId,
        @AuthenticationPrincipal PrincipalDetails userDetails,
        @RequestBody QnACommentReportCreateRequestDto request
) {
    Long reporterId = userDetails.getUser().getId();
    qnACommentReportService.reportPost(commentId, reporterId, request);

    Map<String, Object> body = new HashMap<>();
    body.put("message", "신고가 접수되었습니다.");

    return ResponseEntity.ok(body);
}

}
 

