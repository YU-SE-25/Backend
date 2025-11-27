package com.unide.backend.domain.studygroup.member.repository;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import java.util.List;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupLogRepository extends JpaRepository<StudyGroupLog, Long> {

    Page<StudyGroupLog> findByGroup_GroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    List<StudyGroupLog> findTop20ByGroupOrderByCreatedAtDesc(StudyGroup group);
}