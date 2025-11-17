// CodeReview 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.review.repository;

import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.submissions.entity.Submissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    Page<CodeReview> findBySubmissionOrderByCreatedAtDesc(Submissions submission, Pageable pageable);
}
