// Submissions 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.submissions.repository;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionsRepository extends JpaRepository<Submissions, Long> {
    // 사용자, 문제, 상태로 제출 내역 조회
    Optional<Submissions> findByUserAndProblemAndStatus(User user, Problems problem, SubmissionStatus status);
}
