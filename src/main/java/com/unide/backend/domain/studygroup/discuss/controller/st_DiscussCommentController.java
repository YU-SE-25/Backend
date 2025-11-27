package com.unide.backend.domain.studygroup.discuss.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussCommentRequest;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussCommentResponse;
import com.unide.backend.domain.studygroup.discuss.service.st_DiscussCommentService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studygroup/{groupId}/discuss")
public class st_DiscussCommentController {

    private final st_DiscussCommentService discussCommentService;

    // ===== 특정 게시글 댓글 목록 조회 =====
    // GET /api/studygroup/{groupId}/discuss/{postId}/comments
    @GetMapping("/{postId}/comments")
    public List<st_DiscussCommentResponse> list(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        // groupId는 필요하면 서비스에 추가로 넘기도록 나중에 확장 가능
        return discussCommentService.getCommentsByPost(postId, viewerId);
    }

    // ===== 특정 댓글 단건 조회 =====
    // GET /api/studygroup/{groupId}/discuss/comment/{commentId}
    @GetMapping("/comment/{commentId}")
    public st_DiscussCommentResponse detail(
            @PathVariable Long groupId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return discussCommentService.getComment(commentId, viewerId);
    }

    // ===== 특정 게시글에 댓글 등록 (대댓글 포함) =====
    // POST /api/studygroup/{groupId}/discuss/{postId}/comments
    // body: { "contents": "...", "anonymity": false, "parent_id": 201, "is_private": false }
    @PostMapping("/{postId}/comments")
    public st_DiscussCommentResponse create(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody st_DiscussCommentRequest request
    ) {
        Long authorId = userDetails.getUser().getId();
        return discussCommentService.createComment(postId, authorId, request);
    }

    // ===== 댓글 수정 =====
    // PUT /api/studygroup/{groupId}/discuss/comment/{commentId}
    @PutMapping("/comment/{commentId}")
    public st_DiscussCommentResponse update(
            @PathVariable Long groupId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody st_DiscussCommentRequest request
    ) {
        Long authorId = userDetails.getUser().getId();
        return discussCommentService.updateComment(commentId, authorId, request);
    }

    // ===== 댓글 삭제 =====
    // DELETE /api/studygroup/{groupId}/discuss/comment/{commentId}
    @DeleteMapping("/comment/{commentId}")
    public Map<String, Object> delete(
            @PathVariable Long groupId,
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

    // ===== 특정 댓글에 좋아요 토글 =====
    // POST /api/studygroup/{groupId}/discuss/comment/{commentId}/like
    @PostMapping("/comment/{commentId}/like")
    public st_DiscussCommentResponse like(
            @PathVariable Long groupId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return discussCommentService.toggleLike(commentId, userId);
    }
}
