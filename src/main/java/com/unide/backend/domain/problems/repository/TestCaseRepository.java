// TestCase 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.problems.repository;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    // 특정 문제의 모든 테스트 케이스 조회
    List<TestCase> findAllByProblem(Problems problem);
}
