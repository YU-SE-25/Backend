package com.unide.backend.domain.mainpage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.mainpage.entity.ReputationEvent; // 이미 있던 엔티티

public interface ReputationRankRepository extends JpaRepository<ReputationEvent, Long> {
    @Query(value = """
        SELECT
            us.user_id   AS userId,
            u.nickname   AS nickname,
            us.ranking   AS ranking,   -- user_stats.ranking
            0            AS delta,     -- 아직 주간 변동 계산 안 하니까 0
            us.rating    AS rating     -- user_stats.rating
        FROM user_stats us
        JOIN users u
          ON u.id = us.user_id
        WHERE u.role != 'MANAGER'
        ORDER BY us.rating DESC
        LIMIT :size
        """, nativeQuery = true)
    List<ReputationRankProjection> findRankTop(@Param("size") int size);
}
