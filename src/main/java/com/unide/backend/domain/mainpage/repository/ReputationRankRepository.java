package com.unide.backend.domain.mainpage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.user.entity.User;

public interface ReputationRankRepository extends JpaRepository<User, Long> {

    @Query(value = """
        SELECT 
            re.user_id AS userId,
            re.rank AS rank,
            re.delta AS delta
        FROM reputation_events re
        WHERE re.created_at = (
            SELECT MAX(r2.created_at) 
            FROM reputation_events r2
        )
        ORDER BY re.rank ASC
        LIMIT :size
        """, nativeQuery = true)
    List<ReputationRankProjection> findLatestRankTop(@Param("size") int size);
}
