package com.unide.backend.domain.studygroup.discuss.repository;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPoll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface st_DiscussPollRepository extends JpaRepository<st_DiscussPoll, Long> {

    Optional<st_DiscussPoll> findByPostId(Long postId);
}
