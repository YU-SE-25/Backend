package com.unide.backend.domain.studygroup.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.group.entity.GroupStudyMember;
import com.unide.backend.domain.studygroup.group.entity.GroupStudyMemberId;
public interface StudyGroupMemberQueryRepository
        extends JpaRepository<GroupStudyMember, GroupStudyMemberId> {

    // 그룹별 멤버 조회
    List<GroupStudyMember> findByIdGroupId(Long groupId);

    // 특정 멤버 존재 여부
    boolean existsByIdGroupIdAndIdMemberId(Long groupId, Long memberId);

    // 그룹 멤버 전체 삭제
    void deleteByIdGroupId(Long groupId);
}

