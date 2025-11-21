package com.unide.backend.domain.discuss.repository;

import com.unide.backend.domain.discuss.entity.DiscussTag;
import com.unide.backend.domain.discuss.entity.DiscussTagId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface DiscussTagRepository extends JpaRepository<DiscussTag, DiscussTagId> {
    List<DiscussTag> findByPostId(Long postId);

    void deleteByPostId(Long postId);
}
