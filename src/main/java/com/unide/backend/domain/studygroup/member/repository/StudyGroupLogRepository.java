package com.unide.backend.domain.studygroup.member.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupLog;

public interface StudyGroupLogRepository extends JpaRepository<StudyGroupLog, Long> {

    Page<StudyGroupLog> findByGroup_GroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    List<StudyGroupLog> findTop20ByGroupOrderByCreatedAtDesc(StudyGroup group);

    // ✅ 해당 그룹에 로그가 하나라도 있는지 여부
    boolean existsByGroup_GroupId(Long groupId);
        void deleteByGroup_GroupId(Long groupId);

}
