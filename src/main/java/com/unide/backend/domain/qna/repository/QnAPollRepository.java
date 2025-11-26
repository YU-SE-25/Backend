package com.unide.backend.domain.qna.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnAPoll;

public interface QnAPollRepository extends JpaRepository<QnAPoll, Long> {

    Optional<QnAPoll> findByPostId(Long postId);
}
