package com.unide.backend.domain.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussLike;
import com.unide.backend.domain.discuss.entity.DiscussLikeId;

public interface DiscussLikeRepository
        extends JpaRepository<DiscussLike, DiscussLikeId> {

    boolean existsByIdPostIdAndIdLikerId(Long postId, Long likerId);

    void deleteByIdPostIdAndIdLikerId(Long postId, Long likerId);
}
