package com.unide.backend.domain.studygroup.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussLike;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussLikeId;

public interface st_DiscussLikeRepository extends JpaRepository<st_DiscussLike, st_DiscussLikeId> {

    boolean existsByIdPostIdAndIdLikerIdAndIdGroupId(Long postId, Long likerId, Long groupId);

    void deleteByIdPostIdAndIdLikerIdAndIdGroupId(Long postId, Long likerId, Long groupId);
}
