package com.unide.backend.domain.qna.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.qna.dto.QnACommentRequest;
import com.unide.backend.domain.qna.dto.QnACommentResponse;
import com.unide.backend.domain.qna.entity.QnAComment;
import com.unide.backend.domain.qna.entity.QnACommentLike;
import com.unide.backend.domain.qna.repository.QnACommentLikeRepository;
import com.unide.backend.domain.qna.repository.QnACommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QnACommentService {
    
    private final QnACommentRepository qnaCommentRepository;
    private final QnACommentLikeRepository likeRepository;   

    
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

        boolean privatePost = request.getPrivatePost() != null
                ? request.getPrivatePost()
                : false;

        QnAComment comment = QnAComment.builder()
                .postId(postId)
                .authorId(authorId)
                .anonymous(request.isAnonymity())
                .parentCommentId(request.getParentId())
                // 👉 여기 DTO 메서드 이름을 네가 실제로 가진 걸로 맞춰라
                .content(request.getContents())   // 만약 DTO가 getContent()면 이 줄을 바꿔
                .privatePost(privatePost)
                .likeCount(0)
                .build();

        QnAComment saved = qnaCommentRepository.save(comment);

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

        // 먼저 좋아요 레코드 제거 (FK 제약 때문)
        likeRepository.deleteByCommentIdAndLikerId(commentId, authorId); // 작성자 것도 있으면 삭제
        // 다른 사람 좋아요까지 지우려면 아래 메서드 하나 더 만들어야 함
        // likeRepository.deleteAllByCommentId(commentId);

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
