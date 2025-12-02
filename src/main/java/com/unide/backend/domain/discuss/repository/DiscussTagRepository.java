package com.unide.backend.domain.discuss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussTag;
import com.unide.backend.domain.discuss.entity.DiscussTagId;

public interface DiscussTagRepository extends JpaRepository<DiscussTag, DiscussTagId> {

    List<DiscussTag> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    List<DiscussTag> findByTagId(Long tagId);
}
