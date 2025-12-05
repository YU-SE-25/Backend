package com.unide.backend.domain.mainpage.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.review.entity.CodeReview; // 실제 엔티티 이름에 맞게 수정

public interface CodeReviewRankRepository extends JpaRepository<CodeReview, Long> {

    @Query(value = """
        SELECT
            cr.id          AS id,
            cr.reviewer_id AS authorId,
            u.nickname     AS nickname,   -- ⭐ 닉네임
            cr.vote_count  AS vote
        FROM code_review cr
        JOIN users u ON u.id = cr.reviewer_id
        WHERE cr.created_at >= :from
          AND cr.created_at < :to
        ORDER BY cr.vote_count DESC, cr.created_at ASC
        LIMIT :size
        """, nativeQuery = true)
    List<CodeReviewRankProjection> findTopReviewsByVotes(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("size") int size
    );
}
