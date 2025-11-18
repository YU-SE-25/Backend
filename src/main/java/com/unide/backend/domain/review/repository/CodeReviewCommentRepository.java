// CodeReviewComment 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.review.repository;

import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.entity.CodeReviewComment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CodeReviewCommentRepository extends JpaRepository<CodeReviewComment, Long> {
    @Query("SELECT crc FROM CodeReviewComment crc JOIN FETCH crc.commenter " +
           "WHERE crc.review = :review AND crc.parentComment IS NULL " +
           "ORDER BY crc.createdAt ASC")
    Page<CodeReviewComment> findByReviewAndParentCommentIsNull(@Param("review") CodeReview review, Pageable pageable);
}
