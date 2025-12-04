package com.unide.backend.domain.studygroup.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.problem.entity.StudyGroupProblem;
public interface StudyGroupProblemRepository extends JpaRepository<StudyGroupProblem, Long> {

    List<StudyGroupProblem> findByProblemList_Id(Long problemListId);

    void deleteByProblemList_Id(Long problemListId);

    // ✅ problemList → group → groupId를 타고 들어가는 이름
    void deleteByProblemList_Group_GroupId(Long groupId);
}

