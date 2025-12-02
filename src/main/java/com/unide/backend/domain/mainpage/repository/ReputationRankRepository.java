package com.unide.backend.domain.mainpage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.mainpage.entity.ReputationEvent;

public interface ReputationRankRepository extends JpaRepository<ReputationEvent, Long> {
@Query(value = """
    SELECT
        user_id AS userId,
        ranking AS ranking,
        rating AS rating,
        0 AS delta
    FROM user_stats
    ORDER BY rating DESC
    LIMIT :size
""", nativeQuery = true)
List<ReputationRankProjection> findRankTop(@Param("size") int size);

    
}
