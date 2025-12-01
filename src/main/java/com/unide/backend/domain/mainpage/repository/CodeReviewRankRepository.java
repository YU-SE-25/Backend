// src/main/java/com/unide/backend/domain/mainpage/repository/CodeReviewRankRepository.java
package com.unide.backend.domain.mainpage.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.user.entity.User;

public interface CodeReviewRankRepository extends JpaRepository<User, Long> {

    @Query(value = """
        SELECT
            cr.id        AS id,
            cr.reviewer_id AS authorId,
            cr.vote_count AS vote
        FROM code_review cr
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
