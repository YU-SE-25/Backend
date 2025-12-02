package com.unide.backend.domain.discuss.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.Discuss;

public interface DiscussRepository extends JpaRepository<Discuss, Long> {

    Page<Discuss> findAll(Pageable pageable);

    List<Discuss> findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(
            String titleKeyword,
            String contentsKeyword
    );
        Page<Discuss> findByPostIdIn(List<Long> postIds, Pageable pageable);

}
