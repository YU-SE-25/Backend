package com.unide.backend.domain.studygroup.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.problem.entity.StudyGroupProblemList;

public interface StudyGroupProblemListRepository
        extends JpaRepository<StudyGroupProblemList, Long> {

    // StudyGroupProblemList.group.groupId 기준으로 찾는 메서드
    List<StudyGroupProblemList> findByGroup_GroupId(Long groupId);   // ✅
     // 해당 그룹에 문제 지정 내역이 하나라도 있는지 체크
    boolean existsByGroup_GroupId(Long groupId);
        void deleteByGroup_GroupId(Long groupId);


   
}
