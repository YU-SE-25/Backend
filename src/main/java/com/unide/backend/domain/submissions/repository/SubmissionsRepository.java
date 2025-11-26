// Submissions 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.submissions.repository;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubmissionsRepository extends JpaRepository<Submissions, Long> {
    // 사용자, 문제, 상태로 제출 내역 조회
    Optional<Submissions> findByUserAndProblemAndStatus(User user, Problems problem, SubmissionStatus status);
    
    // 특정 문제의 전체 제출 수
    @Query("SELECT COUNT(s) FROM Submissions s WHERE s.problem.id = :problemId")
    Long countByProblemId(@Param("problemId") Long problemId);
    
    // 특정 문제의 정답 제출 수
    @Query("SELECT COUNT(s) FROM Submissions s WHERE s.problem.id = :problemId AND s.status = 'CA'")
    Long countAcceptedByProblemId(@Param("problemId") Long problemId);
    
    // 사용자가 해결한 문제 ID 목록
    @Query("SELECT DISTINCT s.problem.id FROM Submissions s WHERE s.user.id = :userId AND s.status = 'CA'")
    List<Long> findSolvedProblemsByUserId(@Param("userId") Long userId);
    
    // 사용자의 최근 제출 내역
    @Query("SELECT s FROM Submissions s WHERE s.user.id = :userId ORDER BY s.submittedAt DESC")
    List<Submissions> findRecentSubmissionsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자의 전체 제출 수
    Long countByUserId(Long userId);
    
    // 사용자가 해결한 문제 수
    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM Submissions s WHERE s.user.id = :userId AND s.status = 'CA'")
    Long countSolvedByUserId(@Param("userId") Long userId);

    // 특정 사용자, 특정 문제, 특정 상태(CA)인 제출 중 가장 긴 실행 시간(runtime) 조회
    @Query("SELECT MAX(s.runtime) FROM Submissions s WHERE s.user = :user AND s.problem = :problem AND s.status = :status")
    Optional<Integer> findMaxRuntimeByUserAndProblemAndStatus(@Param("user") User user, 
                                                              @Param("problem") Problems problem, 
                                                              @Param("status") SubmissionStatus status);

    // 내 모든 제출 이력 조회 (최신순, 문제 정보 Fetch Join)
    @Query("SELECT s FROM Submissions s JOIN FETCH s.problem WHERE s.user = :user ORDER BY s.submittedAt DESC")
    Page<Submissions> findAllByUser(@Param("user") User user, Pageable pageable);

    // 특정 문제에 대한 내 제출 이력 조회 (최신순, 문제 정보 Fetch Join)
    @Query("SELECT s FROM Submissions s JOIN FETCH s.problem WHERE s.user = :user AND s.problem = :problem ORDER BY s.submittedAt DESC")
    Page<Submissions> findAllByUserAndProblem(@Param("user") User user, @Param("problem") Problems problem, Pageable pageable);
}
