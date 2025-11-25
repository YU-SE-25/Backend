package com.unide.backend.domain.qna.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnACommentLike;
import com.unide.backend.domain.qna.entity.QnACommentLikeId;

public interface QnACommentLikeRepository 
 extends JpaRepository<QnACommentLike, QnACommentLikeId>{
    
    boolean existsByCommentIdAndLikerId(Long commentId, Long likerId);

    void deleteByCommentIdAndLikerId(Long commentId, Long likerId);

    int countByCommentId(Long commentId);


}
