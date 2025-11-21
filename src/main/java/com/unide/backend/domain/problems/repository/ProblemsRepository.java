// Problems 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.problems.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.Problems;

public interface ProblemsRepository extends JpaRepository<Problems, Long> {
    
    Page<Problems> findByTitleContaining(String title, Pageable pageable);
    
    Page<Problems> findByDifficulty(ProblemDifficulty difficulty, Pageable pageable);
    
    Page<Problems> findByTitleContainingAndDifficulty(String title, ProblemDifficulty difficulty, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Submissions s WHERE s.user.id = :userId AND s.problem.id = :problemId AND s.status = 'CA'")
    boolean isSolvedByUser(@Param("userId") Long userId, @Param("problemId") Long problemId);
}