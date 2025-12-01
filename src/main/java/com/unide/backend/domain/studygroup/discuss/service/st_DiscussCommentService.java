package com.unide.backend.domain.studygroup.discuss.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussCommentRequest;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussCommentResponse;
import com.unide.backend.domain.studygroup.discuss.entity.st_Discuss;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussComment;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussCommentLike;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussCommentLikeRepository;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussCommentRepository;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class st_DiscussCommentService {

    private final st_DiscussCommentRepository discussCommentRepository;
    private final st_DiscussCommentLikeRepository likeRepository;
    private final st_DiscussRepository discussRepository;   // 토론글(게시글) 저장소
    private final UserRepository userRepository;

    // ================================
    // 작성자 이름 조회 유틸
    // ================================
    private String resolveAuthorName(Long authorId) {
        return userRepository.findById(authorId)
                .map(User::getNickname) // 필요한 경우 getName() 등으로 변경
                .orElse("알 수 없음");
    }

    // ===== 특정 게시글 댓글 목록 조회 =====
    @Transactional(readOnly = true)
    public List<st_DiscussCommentResponse> getCommentsByPost(Long postId, Long viewerId) {

        List<st_DiscussComment> commentList =
                discussCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return commentList.stream()
                .map(c -> {
                    boolean viewerLiked = false;
                    if (viewerId != null) {
                        viewerLiked = likeRepository.existsByCommentIdAndLikerId(
                                c.getCommentId(), viewerId);
                    }

                    String authorName = resolveAuthorName(c.getAuthorId());

                    return st_DiscussCommentResponse.fromEntity(
                            c,
                            viewerLiked,
                            null,
                            authorName
                    );
                })
                .collect(Collectors.toList());
    }

    // ===== 단일 댓글 조회 =====
    @Transactional(readOnly = true)
    public st_DiscussCommentResponse getComment(Long commentId, Long viewerId) {
        st_DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean viewerLiked = false;
        if (viewerId != null) {
            viewerLiked = likeRepository.existsByCommentIdAndLikerId(commentId, viewerId);
        }

        String authorName = resolveAuthorName(comment.getAuthorId());

        return st_DiscussCommentResponse.fromEntity(
                comment,
                viewerLiked,
                null,
                authorName
        );
    }

    // ===== 댓글 생성 (대댓글 포함) =====
    public st_DiscussCommentResponse createComment(Long postId,
                                                   Long authorId,
                                                   st_DiscussCommentRequest request) {

        // 1) 게시글 존재 확인 + commentCount 증가 대상
        st_Discuss post = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + postId));

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : false;

        // 2) 댓글 생성 & 저장
        st_DiscussComment comment = st_DiscussComment.builder()
                .postId(postId)
                .authorId(authorId)
                .anonymous(false) // ⭐ 익명 기능 사용 안 함
                .parentCommentId(request.getParentId())
                .content(request.getContents())
                .privatePost(privatePost)
                .likeCount(0)
                .build();

        st_DiscussComment saved = discussCommentRepository.save(comment);

        // 3) 게시글 댓글 수 +1
        post.setCommentCount(post.getCommentCount() + 1);

        String message = (request.getParentId() == null)
                ? "댓글이 등록되었습니다."
                : "대댓글이 등록되었습니다.";

        String authorName = resolveAuthorName(saved.getAuthorId());

        return st_DiscussCommentResponse.fromEntity(
                saved,
                false,
                message,
                authorName
        );
    }

    // ===== 댓글 수정 =====
    public st_DiscussCommentResponse updateComment(Long commentId,
                                                   Long authorId,
                                                   st_DiscussCommentRequest request) {
        st_DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContents());

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : comment.isPrivatePost();

        comment.setPrivatePost(privatePost);

        String authorName = resolveAuthorName(comment.getAuthorId());

        return st_DiscussCommentResponse.fromEntity(
                comment,
                false,
                "댓글이 수정되었습니다.",
                authorName
        );
    }

    // ===== 댓글 삭제 =====
    public void deleteComment(Long commentId, Long authorId) {
        st_DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        // 1) 게시글 댓글 수 -1
        st_Discuss post = discussRepository.findById(comment.getPostId())
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + comment.getPostId()));

        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        // 2) 좋아요 삭제 (현재는 이 유저가 누른 좋아요만)
        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId);

        // 3) 댓글 삭제
        discussCommentRepository.delete(comment);
    }

    // ===== 댓글 좋아요 토글 =====
    public st_DiscussCommentResponse toggleLike(Long commentId, Long userId) {

        st_DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean alreadyLiked = likeRepository.existsByCommentIdAndLikerId(commentId, userId);

        boolean viewerLiked;

        if (alreadyLiked) {
            // 좋아요 취소
            likeRepository.deleteByCommentIdAndLikerId(commentId, userId);
            comment.setLikeCount(comment.getLikeCount() - 1);
            viewerLiked = false;
        } else {
            // 좋아요 추가
            st_DiscussCommentLike like = st_DiscussCommentLike.builder()
                    .commentId(commentId)
                    .likerId(userId)
                    .build();

            likeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            viewerLiked = true;
        }

        String authorName = resolveAuthorName(comment.getAuthorId());

        return st_DiscussCommentResponse.fromEntity(
                comment,
                viewerLiked,
                viewerLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.",
                authorName
        );
    }
}
