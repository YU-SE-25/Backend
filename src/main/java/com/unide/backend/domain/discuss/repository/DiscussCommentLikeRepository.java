package com.unide.backend.domain.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussCommentLike;
import com.unide.backend.domain.discuss.entity.DiscussCommentLikeId;

public interface DiscussCommentLikeRepository
        extends JpaRepository<DiscussCommentLike, DiscussCommentLikeId> {

    boolean existsByCommentIdAndLikerId(Long commentId, Long likerId);

    void deleteByCommentIdAndLikerId(Long commentId, Long likerId);

    int countByCommentId(Long commentId);

    // 필요하면 전체 삭제용
    // void deleteAllByCommentId(Long commentId);
}
