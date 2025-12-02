package com.unide.backend.domain.qna.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.domain.qna.dto.QnACommentRequest;
import com.unide.backend.domain.qna.dto.QnACommentResponse;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnAComment;
import com.unide.backend.domain.qna.entity.QnACommentLike;
import com.unide.backend.domain.qna.repository.QnACommentLikeRepository;
import com.unide.backend.domain.qna.repository.QnACommentRepository;
import com.unide.backend.domain.qna.repository.QnARepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QnACommentService {
    
    private final QnACommentRepository qnaCommentRepository;
    private final QnACommentLikeRepository likeRepository;  
    private final QnARepository qnaRepository;
    private final StatsService statsService;

    // ⭐ 추가: 작성자 이름 조회용
    private final UserRepository userRepository;

    private String resolveAuthorName(Long authorId) {
        return userRepository.findById(authorId)
                .map(User::getNickname) // ⚠ user.getName() 이면 변경
                .orElse("알 수 없음");
    }

    // ================================
    // 📌 특정 게시글 댓글 목록 조회
    // ================================
    @Transactional(readOnly = true)
    public List<QnACommentResponse> getCommentsByPost(Long postId, Long viewerId) {

        List<QnAComment> commentList =
                qnaCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return commentList.stream()
                .map(c -> {
                    boolean viewerLiked = false;

                    if (viewerId != null) {
                        viewerLiked = likeRepository.existsByCommentIdAndLikerId(
                                c.getCommentId(), viewerId);
                    }

                    String authorName = resolveAuthorName(c.getAuthorId());

                    return QnACommentResponse.fromEntity(
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
    public QnACommentResponse getComment(Long commentId, Long viewerId) {
        QnAComment comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean viewerLiked = false;

        if (viewerId != null) {
            viewerLiked = likeRepository.existsByCommentIdAndLikerId(commentId, viewerId);
        }

        String authorName = resolveAuthorName(comment.getAuthorId());

        return QnACommentResponse.fromEntity(
                comment,
                viewerLiked,
                null,
                authorName
        );
    }

    // ================================
    // 📌 댓글 생성
    // ================================
    public QnACommentResponse createComment(Long postId,
                                            Long authorId,
                                            QnACommentRequest request) {

        QnA post = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + postId));

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : false;

        QnAComment comment = QnAComment.builder()
                .postId(postId)
                .authorId(authorId)
                .anonymous(request.isAnonymity())
                .parentCommentId(request.getParentId())
                .content(request.getContents())
                .privatePost(privatePost)
                .likeCount(0)
                .build();

        QnAComment saved = qnaCommentRepository.save(comment);

       
        Long postAuthorId = post.getAuthor().getId();   // ✅ QnA 작성자 id
        statsService.onQnaCommentCreated(postAuthorId); // QnA 글쓴이에게 점수 +3
     


        post.setCommentCount(post.getCommentCount() + 1);

        String message = (request.getParentId() == null)
                ? "댓글이 등록되었습니다."
                : "대댓글이 등록되었습니다.";

        String authorName = resolveAuthorName(saved.getAuthorId());

        return QnACommentResponse.fromEntity(
                saved,
                false,
                message,
                authorName
        );
    }

    // ================================
    // 📌 댓글 수정
    // ================================
    public QnACommentResponse updateComment(Long commentId,
                                            Long authorId,
                                            QnACommentRequest request) {

        QnAComment comment = qnaCommentRepository.findById(commentId)
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

        return QnACommentResponse.fromEntity(
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

        QnAComment comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        QnA post = qnaRepository.findById(comment.getPostId())
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + comment.getPostId()));

        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId);

        qnaCommentRepository.delete(comment);
    }

    // ================================
    // 📌 좋아요 토글
    // ================================
    public QnACommentResponse toggleLike(Long commentId, Long userId) {

        QnAComment comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean alreadyLiked = likeRepository.existsByCommentIdAndLikerId(commentId, userId);

        boolean viewerLiked;

        if (alreadyLiked) {
            likeRepository.deleteByCommentIdAndLikerId(commentId, userId);
            comment.setLikeCount(comment.getLikeCount() - 1);
            viewerLiked = false;
        } else {
            QnACommentLike like = QnACommentLike.builder()
                    .commentId(commentId)
                    .likerId(userId)
                    .build();

            likeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            viewerLiked = true;
            statsService.onQnaCommentLiked(comment.getAuthorId());

        }

        String authorName = resolveAuthorName(comment.getAuthorId());

        return QnACommentResponse.fromEntity(
                comment,
                viewerLiked,
                viewerLiked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.",
                authorName
        );
    }
}
