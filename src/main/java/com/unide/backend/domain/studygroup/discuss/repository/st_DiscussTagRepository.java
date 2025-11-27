package com.unide.backend.domain.studygroup.discuss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussTag;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussTagId;

public interface st_DiscussTagRepository extends JpaRepository<st_DiscussTag, st_DiscussTagId> {

    void deleteByGroupIdAndPostId(Long groupId, Long postId);

    List<st_DiscussTag> findByGroupIdAndPostId(Long groupId, Long postId);
}
