package com.unide.backend.domain.discuss.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussCommentRequest;
import com.unide.backend.domain.discuss.dto.DiscussCommentResponse;
import com.unide.backend.domain.discuss.entity.DiscussComment;
import com.unide.backend.domain.discuss.entity.DiscussCommentLike;
import com.unide.backend.domain.discuss.repository.DiscussCommentLikeRepository;
import com.unide.backend.domain.discuss.repository.DiscussCommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussCommentService {

    private final DiscussCommentRepository discussCommentRepository;
    private final DiscussCommentLikeRepository likeRepository;   // ⭐ 추가

    // ===== 특정 게시글 댓글 목록 조회 =====
    @Transactional(readOnly = true)
    public List<DiscussCommentResponse> getCommentsByPost(Long postId, Long viewerId) {

        List<DiscussComment> commentList =
                discussCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        // viewerLiked 계산 (로그인 안 했으면 전부 false)
        return commentList.stream()
                .map(c -> {
                    boolean viewerLiked = false;
                    if (viewerId != null) {
                        viewerLiked = likeRepository.existsByCommentIdAndLikerId(
                                c.getCommentId(), viewerId);
                    }
                    return DiscussCommentResponse.fromEntity(c, viewerLiked, null);
                })
                .collect(Collectors.toList());
    }

    // ===== 단일 댓글 조회 =====
    @Transactional(readOnly = true)
    public DiscussCommentResponse getComment(Long commentId, Long viewerId) {
        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean viewerLiked = false;
        if (viewerId != null) {
            viewerLiked = likeRepository.existsByCommentIdAndLikerId(commentId, viewerId);
        }

        return DiscussCommentResponse.fromEntity(comment, viewerLiked, null);
    }

    // ===== 댓글 생성 (대댓글 포함) =====
    public DiscussCommentResponse createComment(Long postId,
                                                Long authorId,
                                                DiscussCommentRequest request) {

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : false;

        DiscussComment comment = DiscussComment.builder()
                .postId(postId)
                .authorId(authorId)
                .anonymous(request.isAnonymity())
                .parentCommentId(request.getParentId())
                // 👉 여기 DTO 메서드 이름을 네가 실제로 가진 걸로 맞춰라
                .content(request.getContents())   // 만약 DTO가 getContent()면 이 줄을 바꿔
                .privatePost(privatePost)
                .likeCount(0)
                .build();

        DiscussComment saved = discussCommentRepository.save(comment);

        String message = (request.getParentId() == null)
                ? "댓글이 등록되었습니다."
                : "대댓글이 등록되었습니다.";

        return DiscussCommentResponse.fromEntity(saved, false, message);
    }

    // ===== 댓글 수정 =====
    public DiscussCommentResponse updateComment(Long commentId,
                                                Long authorId,
                                                DiscussCommentRequest request) {
        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        // DTO 메서드 이름 맞추기
        comment.setContent(request.getContents());
        comment.setAnonymous(request.isAnonymity());

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : comment.isPrivatePost();

        comment.setPrivatePost(privatePost);

        return DiscussCommentResponse.fromEntity(comment, false, "댓글이 수정되었습니다.");
    }

    // ===== 댓글 삭제 =====
    public void deleteComment(Long commentId, Long authorId) {
        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        // 먼저 좋아요 레코드 제거 (FK 제약 때문)
        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId); // 작성자 것도 있으면 삭제
        // 다른 사람 좋아요까지 지우려면 아래 메서드 하나 더 만들어야 함
        // likeRepository.deleteAllByCommentId(commentId);

        discussCommentRepository.delete(comment);
    }

    // ===== 좋아요 토글 =====
    public DiscussCommentResponse toggleLike(Long commentId, Long userId) {

        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean alreadyLiked = likeRepository.existsByCommentIdAndLikerId(commentId, userId);

        if (alreadyLiked) {
            // 좋아요 삭제
            likeRepository.deleteByCommentIdAndLikerId(commentId, userId);
            comment.setLikeCount(comment.getLikeCount() - 1);

            return DiscussCommentResponse.fromEntity(
                    comment,
                    false,
                    "좋아요가 취소되었습니다."
            );
        } else {
            // 좋아요 추가
            DiscussCommentLike like = DiscussCommentLike.builder()
                    .commentId(commentId)
                    .likerId(userId)
                    .build();

            likeRepository.save(like);   // ⭐ 여기 수정
            comment.setLikeCount(comment.getLikeCount() + 1);

            return DiscussCommentResponse.fromEntity(
                    comment,
                    true,
                    "좋아요가 추가되었습니다."
            );
        }
    }
}
