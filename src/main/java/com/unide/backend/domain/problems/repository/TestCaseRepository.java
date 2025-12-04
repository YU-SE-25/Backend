// TestCase 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.problems.repository;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    void deleteByProblem(Problems problem);

    List<TestCase> findAllByProblem(Problems problem);
}