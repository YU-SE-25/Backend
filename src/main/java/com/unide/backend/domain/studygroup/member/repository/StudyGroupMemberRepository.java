package com.unide.backend.domain.studygroup.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.member.entity.StudyGroupMember;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupMemberId;

public interface StudyGroupMemberRepository
        extends JpaRepository<StudyGroupMember, StudyGroupMemberId> {

    List<StudyGroupMember> findByGroup_GroupId(Long groupId);

    List<StudyGroupMember> findByMember_Id(Long memberId);

    Optional<StudyGroupMember> findByGroup_GroupIdAndMember_Id(Long groupId, Long memberId);

    boolean existsByGroup_GroupIdAndMember_Id(Long groupId, Long memberId);

    long countByGroup_GroupId(Long groupId);
}
