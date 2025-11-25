package com.unide.backend.domain.discuss.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussPoll;

public interface DiscussPollRepository extends JpaRepository<DiscussPoll, Long> {

    // postId로 Poll 조회
    Optional<DiscussPoll> findByPostId(Long postId);
}
