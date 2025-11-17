// CodeReview 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.review.repository;

import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.submissions.entity.Submissions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    @Query("SELECT cr FROM CodeReview cr JOIN FETCH cr.reviewer WHERE cr.submission = :submission ORDER BY cr.createdAt DESC")
    Page<CodeReview> findBySubmission(@Param("submission") Submissions submission, Pageable pageable);
}
