package com.unide.backend.domain.studygroup.group.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.studygroup.group.dto.StudyGroupCreateRequest;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupDetailResponse;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupListItemResponse;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupMemberSummary;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupUpdateRequest;
import com.unide.backend.domain.studygroup.group.entity.GroupStudyMember;
import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupMemberQueryRepository;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupService {

    private final StudyGroupRepository groupRepository;
    private final StudyGroupMemberQueryRepository memberRepository;
    private final UserRepository userRepository;

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
        GroupStudyMember leaderMembership =
                memberRepository.save(GroupStudyMember.of(saved.getGroupId(), leaderId));

        // 리더 User 조회
        User leaderUser = userRepository.findById(leaderId).orElse(null);

        // 리더 요약 DTO
        StudyGroupMemberSummary leaderSummary =
                StudyGroupMemberSummary.from(leaderMembership, leaderUser, leaderId);

        // 생성 직후 상세 응답 (myRole = LEADER)
        return StudyGroupDetailResponse.fromEntity(
                saved,
                leaderSummary,
                List.of(leaderSummary),
                "LEADER"
        );
    }

    // ===== 그룹 목록 조회 =====
   @Transactional(readOnly = true)
public List<StudyGroupListItemResponse> listGroups(Long currentUserId, int pageSize) {

    PageRequest pageRequest = PageRequest.of(0, pageSize);

    // 1) 페이지 조회
    var page = groupRepository.findAll(pageRequest);
    List<StudyGroup> groups = page.getContent();

    // 2) 리더 ID 모아서 한 번에 User 조회
    List<Long> leaderIds = groups.stream()
            .map(StudyGroup::getGroupLeader)
            .filter(id -> id != null)
            .distinct()
            .toList();

    Map<Long, User> leaderMap = userRepository.findAllById(leaderIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    // 3) 응답 변환
    return groups.stream()
            .map(g -> {
                // 내 역할 계산
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

                // 리더 이름 세팅
                String leaderName = null;
                Long leaderId = g.getGroupLeader();
                if (leaderId != null) {
                    User leader = leaderMap.get(leaderId);
                    if (leader != null) {
                        leaderName = leader.getNickname(); // <= 여기 필드 이름에 맞게 수정 (ex. getName())
                    }
                }

                return StudyGroupListItemResponse.fromEntity(g, leaderName, myRole);
            })
            .collect(Collectors.toList());
}


    // ===== 그룹 상세 조회 =====
    @Transactional(readOnly = true)
    public StudyGroupDetailResponse getGroupDetail(Long groupId, Long currentUserId) {

        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음: " + groupId));

        // 1) 이 그룹에 속한 멤버십들 조회
        List<GroupStudyMember> memberships =
                memberRepository.findByIdGroupId(groupId);

        // 2) memberId → User 매핑
        List<Long> memberIds = memberships.stream()
                .map(m -> m.getId().getMemberId())
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Long leaderId = group.getGroupLeader();

        // 3) 전체 멤버 요약 DTO
        List<StudyGroupMemberSummary> memberSummaries = memberships.stream()
                .map(m -> {
                    Long memberId = m.getId().getMemberId();
                    User u = userMap.get(memberId);
                    return StudyGroupMemberSummary.from(m, u, leaderId);
                })
                .collect(Collectors.toList());

        // 4) 리더 DTO
        StudyGroupMemberSummary leaderSummary = memberSummaries.stream()
                .filter(ms -> "LEADER".equals(ms.getRole()))
                .findFirst()
                .orElse(null);

        // 5) 현재 로그인 유저의 역할
        String myRole = "NONE";
        if (currentUserId != null) {
            myRole = memberSummaries.stream()
                    .filter(ms -> ms.getUserId().equals(currentUserId))
                    .map(StudyGroupMemberSummary::getRole)
                    .findFirst()
                    .orElse("NONE");
        }

        return StudyGroupDetailResponse.fromEntity(
                group,
                leaderSummary,
                memberSummaries,
                myRole
        );
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

        // 수정 후 상세 응답 (요청자가 현재 사용자)
        return getGroupDetail(groupId, requesterId);
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
