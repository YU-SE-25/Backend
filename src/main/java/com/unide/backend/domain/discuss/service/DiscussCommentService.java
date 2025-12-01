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
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussCommentService {

    private final DiscussCommentRepository discussCommentRepository;
    private final DiscussCommentLikeRepository likeRepository;
    private final DiscussRepository discussRepository;

    private final UserRepository userRepository; // ⭐ 추가

    // ================================
    // ⭐ 작성자 이름 조회
    // ================================
    private String resolveAuthorName(Long authorId) {
        return userRepository.findById(authorId)
                .map(User::getNickname)  // ⚠ User 엔티티 구조에 맞게 수정 가능
                .orElse("알 수 없음");
    }

    // ================================
    // 📌 특정 게시글 댓글 목록 조회
    // ================================
    @Transactional(readOnly = true)
    public List<DiscussCommentResponse> getCommentsByPost(Long postId, Long viewerId) {

        List<DiscussComment> commentList =
                discussCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return commentList.stream()
                .map(c -> {
                    boolean viewerLiked = false;

                    if (viewerId != null) {
                        viewerLiked = likeRepository.existsByCommentIdAndLikerId(
                                c.getCommentId(), viewerId
                        );
                    }

                    String authorName = resolveAuthorName(c.getAuthorId());

                    return DiscussCommentResponse.fromEntity(
                            c,
                            viewerLiked,
                            null,
                            authorName
                    );
                })
                .collect(Collectors.toList());
    }

    // ================================
    // 📌 단일 댓글 조회
    // ================================
    @Transactional(readOnly = true)
    public DiscussCommentResponse getComment(Long commentId, Long viewerId) {

        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean viewerLiked = false;

        if (viewerId != null) {
            viewerLiked = likeRepository.existsByCommentIdAndLikerId(commentId, viewerId);
        }

        String authorName = resolveAuthorName(comment.getAuthorId());

        return DiscussCommentResponse.fromEntity(
                comment,
                viewerLiked,
                null,
                authorName
        );
    }

    // ================================
    // 📌 댓글 생성
    // ================================
    public DiscussCommentResponse createComment(Long postId,
                                                Long authorId,
                                                DiscussCommentRequest request) {

        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + postId));

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : false;

        DiscussComment comment = DiscussComment.builder()
                .postId(postId)
                .authorId(authorId)
                .anonymous(request.isAnonymity())
                .parentCommentId(request.getParentId())
                .content(request.getContents())
                .privatePost(privatePost)
                .likeCount(0)
                .build();

        DiscussComment saved = discussCommentRepository.save(comment);

        // 댓글 수 증가
        post.setCommentCount(post.getCommentCount() + 1);

        String message = (request.getParentId() == null)
                ? "댓글이 등록되었습니다."
                : "대댓글이 등록되었습니다.";

        String authorName = resolveAuthorName(saved.getAuthorId());

        return DiscussCommentResponse.fromEntity(
                saved,
                false,
                message,
                authorName
        );
    }

    // ================================
    // 📌 댓글 수정
    // ================================
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

        String authorName = resolveAuthorName(comment.getAuthorId());

        return DiscussCommentResponse.fromEntity(
                comment,
                false,
                "댓글이 수정되었습니다.",
                authorName
        );
    }

    // ================================
    // 📌 댓글 삭제
    // ================================
    public void deleteComment(Long commentId, Long authorId) {

        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        Discuss post = discussRepository.findById(comment.getPostId())
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + comment.getPostId()));

        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId);

        discussCommentRepository.delete(comment);
    }

    // ================================
    // 📌 좋아요 토글
    // ================================
    public DiscussCommentResponse toggleLike(Long commentId, Long userId) {

        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean alreadyLiked = likeRepository.existsByCommentIdAndLikerId(commentId, userId);

        boolean viewerLiked;

        if (alreadyLiked) {
            likeRepository.deleteByCommentIdAndLikerId(commentId, userId);
            comment.setLikeCount(comment.getLikeCount() - 1);
            viewerLiked = false;
        } else {
            DiscussCommentLike like = DiscussCommentLike.builder()
                    .commentId(commentId)
                    .likerId(userId)
                    .build();

            likeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            viewerLiked = true;
        }

        String authorName = resolveAuthorName(comment.getAuthorId());

        return DiscussCommentResponse.fromEntity(
                comment,
                viewerLiked,
                viewerLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.",
                authorName
        );
    }
}
