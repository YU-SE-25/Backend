// CodeReviewVote 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.review.repository;

import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.entity.CodeReviewVote;
import com.unide.backend.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeReviewVoteRepository extends JpaRepository<CodeReviewVote, Long> {
    Optional<CodeReviewVote> findByReviewAndVoter(CodeReview review, User voter);
}
