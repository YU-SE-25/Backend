package com.unide.backend.domain.discuss.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussCommentRequest;
import com.unide.backend.domain.discuss.dto.DiscussCommentResponse;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.entity.DiscussComment;
import com.unide.backend.domain.discuss.entity.DiscussCommentLike;
import com.unide.backend.domain.discuss.repository.DiscussCommentLikeRepository;
import com.unide.backend.domain.discuss.repository.DiscussCommentRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussCommentService {

    private final DiscussCommentRepository discussCommentRepository;
    private final DiscussCommentLikeRepository likeRepository;
    private final DiscussRepository discussRepository;   // ⭐ 토론글(게시글) 저장소

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

        // 1) 게시글 존재 확인 + commentCount 증가 대상
        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + postId));

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : false;

        // 2) 댓글 생성 & 저장
        DiscussComment comment = DiscussComment.builder()
                .postId(postId)
                .authorId(authorId)
                .anonymous(request.isAnonymity())
                .parentCommentId(request.getParentId())
                .content(request.getContents())   // DTO가 contents 필드라고 가정
                .privatePost(privatePost)
                .likeCount(0)
                .build();

        DiscussComment saved = discussCommentRepository.save(comment);

        // 3) 게시글 댓글 수 +1
        post.setCommentCount(post.getCommentCount() + 1);

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

        // 1) 게시글 댓글 수 -1
        Discuss post = discussRepository.findById(comment.getPostId())
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + comment.getPostId()));

        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        // 2) 좋아요 삭제 (지금은 작성자의 좋아요만, 필요하면 전체 삭제 메서드 추가)
        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId);
        // 예: likeRepository.deleteAllByCommentId(commentId); 이런 것도 추가 가능

        // 3) 댓글 삭제
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

            likeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);

            return DiscussCommentResponse.fromEntity(
                    comment,
                    true,
                    "좋아요가 추가되었습니다."
            );
        }
    }
}
