package com.unide.backend.domain.submission.repository;

import com.unide.backend.domain.submission.entity.Submission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("SELECT DISTINCT s.problem.problemId FROM Submission s WHERE s.user.id = :userId AND s.status = 'AC' ORDER BY s.problem.problemId")
    List<Long> findSolvedProblemsByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId ORDER BY s.submittedAt DESC")
    List<Submission> findRecentSubmissionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.user.id = :userId AND s.status = 'AC'")
    Long countSolvedByUserId(@Param("userId") Long userId);
}
