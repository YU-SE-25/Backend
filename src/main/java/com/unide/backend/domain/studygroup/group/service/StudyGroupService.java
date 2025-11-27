package com.unide.backend.domain.studygroup.group.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.studygroup.group.dto.StudyGroupCreateRequest;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupDetailResponse;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupListItemResponse;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupMemberSummary;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupUpdateRequest;
import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.studygroup.group.entity.GroupStudyMember;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupMemberQueryRepository;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupService {

    private final StudyGroupRepository groupRepository;
    private final StudyGroupMemberQueryRepository memberRepository;
    // 필요하면 UserRepository 주입해서 이름 채우기

    // ===== 그룹 생성 =====
    public StudyGroupDetailResponse createGroup(Long leaderId,
                                                StudyGroupCreateRequest request) {

        int maxMembers = (request.getMaxMembers() != null)
                ? request.getMaxMembers()
                : 2;

        StudyGroup group = StudyGroup.builder()
                .groupName(request.getGroupName())
                .groupGoal(request.getGroupDescription())
                .groupLeader(leaderId)
                .memberCount(1)
                .groupTo(maxMembers)
                .build();

        StudyGroup saved = groupRepository.save(group);

        // 리더를 멤버로 추가
        memberRepository.save(GroupStudyMember.of(saved.getGroupId(), leaderId));

        // leader / members 응답 구성 (이름은 일단 null)
        StudyGroupMemberSummary leaderSummary = StudyGroupMemberSummary.builder()
                .groupMemberId(leaderId)
                .userId(leaderId)
                .userName(null)
                .role("LEADER")
                .build();

        return StudyGroupDetailResponse.fromEntity(saved, leaderSummary, List.of(leaderSummary));
    }

    // ===== 그룹 목록 조회 =====
    @Transactional(readOnly = true)
    public List<StudyGroupListItemResponse> listGroups(Long currentUserId, int pageSize) {

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        return groupRepository.findAll(pageRequest)
                .stream()
                .map(g -> {
                    String myRole = "NONE";
                    if (currentUserId != null) {
                        if (g.getGroupLeader() != null &&
                                g.getGroupLeader().equals(currentUserId)) {
                            myRole = "LEADER";
                        } else if (memberRepository.existsByIdGroupIdAndIdMemberId(
                                g.getGroupId(), currentUserId)) {
                            myRole = "MEMBER";
                        }
                    }

                    // leaderName 은 나중에 UserRepository 붙이면 채워주면 됨
                    return StudyGroupListItemResponse.fromEntity(g, null, myRole);
                })
                .collect(Collectors.toList());
    }

    // ===== 그룹 상세 조회 =====
    @Transactional(readOnly = true)
    public StudyGroupDetailResponse getGroupDetail(Long groupId) {

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 스터디 그룹이 없습니다. groupId=" + groupId));

        List<GroupStudyMember> members =
                memberRepository.findByIdGroupId(groupId);

        List<StudyGroupMemberSummary> memberDtos = members.stream()
                .map(m -> {
                    Long userId = m.getId().getMemberId();
                    String role = (group.getGroupLeader() != null
                            && group.getGroupLeader().equals(userId))
                            ? "LEADER"
                            : "MEMBER";
                    return StudyGroupMemberSummary.builder()
                            .groupMemberId(userId)
                            .userId(userId)
                            .userName(null) // UserRepository 있으면 채우기
                            .role(role)
                            .build();
                })
                .collect(Collectors.toList());

        StudyGroupMemberSummary leaderDto = memberDtos.stream()
                .filter(m -> "LEADER".equals(m.getRole()))
                .findFirst()
                .orElse(null);

        return StudyGroupDetailResponse.fromEntity(group, leaderDto, memberDtos);
    }

    // ===== 그룹 수정 =====
    public StudyGroupDetailResponse updateGroup(Long groupId,
                                                Long requesterId,
                                                StudyGroupUpdateRequest request) {

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 스터디 그룹이 없습니다. groupId=" + groupId));

        if (group.getGroupLeader() == null ||
                !group.getGroupLeader().equals(requesterId)) {
            throw new IllegalStateException("그룹장만 수정할 수 있습니다.");
        }

        if (request.getGroupName() != null) {
            group.setGroupName(request.getGroupName());
        }
        if (request.getGroupDescription() != null) {
            group.setGroupGoal(request.getGroupDescription());
        }
        if (request.getMaxMembers() != null) {
            group.setGroupTo(request.getMaxMembers());
        }

        // 새 멤버 추가
        if (request.getGroupMemberIds() != null) {
            for (Long userId : request.getGroupMemberIds()) {
                if (!memberRepository.existsByIdGroupIdAndIdMemberId(groupId, userId)) {
                    memberRepository.save(GroupStudyMember.of(groupId, userId));
                    group.setMemberCount(group.getMemberCount() + 1);
                }
            }
        }

        // 수정 후 상세 응답 재사용
        return getGroupDetail(groupId);
    }

    // ===== 그룹 삭제 =====
    public void deleteGroup(Long groupId, Long requesterId) {

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 스터디 그룹이 없습니다. groupId=" + groupId));

        if (group.getGroupLeader() == null ||
                !group.getGroupLeader().equals(requesterId)) {
            throw new IllegalStateException("그룹장만 삭제할 수 있습니다.");
        }

        // 멤버 먼저 삭제
        memberRepository.deleteByIdGroupId(groupId);
        groupRepository.delete(group);
    }
}
