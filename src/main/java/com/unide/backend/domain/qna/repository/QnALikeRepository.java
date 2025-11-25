package com.unide.backend.domain.qna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnALike;
import com.unide.backend.domain.qna.entity.QnALikeId;

public interface QnALikeRepository extends JpaRepository<QnALike, QnALikeId> {

    // 이미 좋아요 눌렀는지 확인
    boolean existsByIdPostIdAndIdLikerId(Long postId, Long likerId);

    // 좋아요 취소
    void deleteByIdPostIdAndIdLikerId(Long postId, Long likerId);

    // 특정 게시글 좋아요 개수
    int countByIdPostId(Long postId);
}
