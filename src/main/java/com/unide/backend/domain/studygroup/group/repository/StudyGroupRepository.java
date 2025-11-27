package com.unide.backend.domain.studygroup.group.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    Page<StudyGroup> findAll(Pageable pageable);
}
