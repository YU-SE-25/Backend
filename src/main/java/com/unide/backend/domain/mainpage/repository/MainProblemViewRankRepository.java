package com.unide.backend.domain.mainpage.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto; 
import com.unide.backend.domain.mainpage.entity.MainProblemViewRankEntity;

// 인터페이스 이름을 MainProblemViewRankRepository로 변경
public interface MainProblemViewRankRepository extends JpaRepository<MainProblemViewRankEntity, Long> {

    /**
     * 특정 날짜에 집계된 주간 조회수 순위 목록을 조회합니다.
     * DTO의 전체 경로를 사용하여 ProblemViewRank Entity와 Problems Entity를 조인하여 데이터를 가져옵니다.
     * @param rankDate 조회 기준 날짜 (problem_view_rank.date)
     * @return 조회수 내림차순으로 정렬된 문제 목록
     */
    @Query("SELECT new com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto(" +
       "pvr.problem.id, pvr.problem.title, pvr.problem.difficulty, pvr.views, pvr.date) " +
       "FROM MainProblemViewRankEntity pvr " +   
       "WHERE pvr.date = :rankDate " +
       "ORDER BY pvr.views DESC")
List<MainProblemViewRankDto> findRankedProblemsByDate(@Param("rankDate") LocalDate rankDate);

}