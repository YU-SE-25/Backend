package com.unide.backend.domain.studygroup.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussCommentLike;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussCommentLikeId;

public interface st_DiscussCommentLikeRepository extends JpaRepository<st_DiscussCommentLike, st_DiscussCommentLikeId> {

    boolean existsByCommentIdAndLikerId(Long commentId, Long likerId);

    void deleteByCommentIdAndLikerId(Long commentId, Long likerId);

    int countByCommentId(Long commentId);

    // 필요하면 전체 삭제용
    // void deleteAllByCommentId(Long commentId);
}