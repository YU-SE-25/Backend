package com.unide.backend.domain.qna.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.qna.dto.QnACommentRequest;
import com.unide.backend.domain.qna.dto.QnACommentResponse;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnAComment;
import com.unide.backend.domain.qna.entity.QnACommentLike;
import com.unide.backend.domain.qna.repository.QnACommentLikeRepository;
import com.unide.backend.domain.qna.repository.QnACommentRepository;
import com.unide.backend.domain.qna.repository.QnARepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QnACommentService {
    
    private final QnACommentRepository qnaCommentRepository;
    private final QnACommentLikeRepository likeRepository;  
    private final QnARepository qnaRepository;
 

    
    // ===== 특정 게시글 댓글 목록 조회 =====
    @Transactional(readOnly = true)
    public List<QnACommentResponse> getCommentsByPost(Long postId, Long viewerId) {

        List<QnAComment> commentList =
                qnaCommentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        // viewerLiked 계산 (로그인 안 했으면 전부 false)
        return commentList.stream()
                .map(c -> {
                    boolean viewerLiked = false;
                    if (viewerId != null) {
                        viewerLiked = likeRepository.existsByCommentIdAndLikerId(
                                c.getCommentId(), viewerId);
                    }
                    return QnACommentResponse.fromEntity(c, viewerLiked, null);
                })
                .collect(Collectors.toList());
    }
    
    // ===== 단일 댓글 조회 =====
    @Transactional(readOnly = true)
    public QnACommentResponse getComment(Long commentId, Long viewerId) {
        QnAComment comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean viewerLiked = false;
        if (viewerId != null) {
            viewerLiked = likeRepository.existsByCommentIdAndLikerId(commentId, viewerId);
        }

        return QnACommentResponse.fromEntity(comment, viewerLiked, null);
    }
// ===== 댓글 생성 (대댓글 포함) =====
public QnACommentResponse createComment(Long postId,
                                        Long authorId,
                                        QnACommentRequest request) {

    // 1) 게시글 존재하는지 확인 + comment_count 증가 대상
    QnA post = qnaRepository.findById(postId)
            .orElseThrow(() ->
                    new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + postId));

    // 2) privatePost 처리
    boolean privatePost = request.getPrivatePost() != null
            ? request.getPrivatePost()
            : false;

    // 3) 댓글 생성
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

    // 4) comment_count 증가
    post.setCommentCount(post.getCommentCount() + 1);

    String message = (request.getParentId() == null)
            ? "댓글이 등록되었습니다."
            : "대댓글이 등록되었습니다.";

    return QnACommentResponse.fromEntity(saved, false, message);
}


    // ===== 댓글 수정 =====
    public QnACommentResponse updateComment(Long commentId,
                                                Long authorId,
                                                QnACommentRequest request) {
        QnAComment comment = qnaCommentRepository.findById(commentId)
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

        return QnACommentResponse.fromEntity(comment, false, "댓글이 수정되었습니다.");
    }
    // ===== 댓글 삭제 =====
    public void deleteComment(Long commentId, Long authorId) {
        QnAComment comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        // 1) 게시글 댓글 수 -1
        QnA post = qnaRepository.findById(comment.getPostId())
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. postId=" + comment.getPostId()));

        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        // 2) 좋아요 정리 (지금은 작성자 것만 지우지만, 필요하면 전체 삭제 메서드 추가)
        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId);

        // 3) 댓글 삭제
        qnaCommentRepository.delete(comment);
    }
  // ===== 좋아요 토글 =====
    public QnACommentResponse toggleLike(Long commentId, Long userId) {

        QnAComment comment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 댓글이 없습니다. commentId=" + commentId));

        boolean alreadyLiked = likeRepository.existsByCommentIdAndLikerId(commentId, userId);

        if (alreadyLiked) {
            // 좋아요 삭제
            likeRepository.deleteByCommentIdAndLikerId(commentId, userId);
            comment.setLikeCount(comment.getLikeCount() - 1);

            return QnACommentResponse.fromEntity(
                    comment,
                    false,
                    "좋아요가 취소되었습니다."
            );
        } else {
            // 좋아요 추가
            QnACommentLike like = QnACommentLike.builder()
                    .commentId(commentId)
                    .likerId(userId)
                    .build();

            likeRepository.save(like);   
            comment.setLikeCount(comment.getLikeCount() + 1);

            return QnACommentResponse.fromEntity(
                    comment,
                    true,
                    "좋아요가 추가되었습니다."
            );
        }
    }

}
