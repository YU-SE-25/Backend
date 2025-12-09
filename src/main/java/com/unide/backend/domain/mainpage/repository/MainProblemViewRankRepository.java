package com.unide.backend.domain.mainpage.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto;
import com.unide.backend.domain.problems.entity.Problems;

public interface MainProblemViewRankRepository extends JpaRepository<Problems, Long> {

    @Query("""
        SELECT new com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto(
            p.id,
            p.title,
            p.difficulty,
            p.viewCount,
            :rankDate
        )
        FROM Problems p
        WHERE p.status = com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED
        ORDER BY p.viewCount DESC
        """)
    List<MainProblemViewRankDto> findRankedProblemsByDate(@Param("rankDate") LocalDate rankDate);
}
