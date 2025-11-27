package com.unide.backend.domain.studygroup.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.problem.entity.StudyGroupProblem;

public interface StudyGroupProblemRepository extends JpaRepository<StudyGroupProblem, Long> {

    // 특정 리스트에 속한 문제들
    List<StudyGroupProblem> findByProblemList_Id(Long problemListId);

    // 리스트 수정 시 기존 문제들 싹 지우고 다시 넣고 싶을 때 사용
    void deleteByProblemList_Id(Long problemListId);
}
