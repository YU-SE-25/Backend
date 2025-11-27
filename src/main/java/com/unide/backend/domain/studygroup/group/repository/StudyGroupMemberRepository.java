package com.unide.backend.domain.studygroup.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.group.entity.StudyGroupMember;
import com.unide.backend.domain.studygroup.group.entity.StudyGroupMemberId;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, StudyGroupMemberId> {

    List<StudyGroupMember> findByIdGroupId(Long groupId);

    boolean existsByIdGroupIdAndIdMemberId(Long groupId, Long memberId);

    long countByIdGroupId(Long groupId);

    void deleteByIdGroupId(Long groupId);
}
