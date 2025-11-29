package com.unide.backend.domain.studygroup.member.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupRepository;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupActivityItemResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupActivityPageResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupCapacityDto;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupJoinResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupKickResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupLeaveResponse;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupActivityType;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupLog;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupMember;
import com.unide.backend.domain.studygroup.member.entity.StudyGroupRefEntityType;
import com.unide.backend.domain.studygroup.member.repository.StudyGroupLogRepository;
import com.unide.backend.domain.studygroup.member.repository.StudyGroupMemberRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupMemberService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final StudyGroupLogRepository studyGroupLogRepository;
    private final UserRepository userRepository;

    /**
     * 스터디 그룹 가입
     */
    public StudyGroupJoinResponse join(Long groupId, Long userId) {

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("StudyGroup not found: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

<<<<<<< HEAD
        // 이미 가입 여부 체크
=======
        // 이미 가입 여부
>>>>>>> bc4f78f ([추가]스터디그룹 로그)
        if (studyGroupMemberRepository.existsByGroup_GroupIdAndMember_Id(groupId, userId)) {
            throw new IllegalStateException("이미 가입된 스터디 그룹입니다.");
        }

        // 정원 체크
        int max = group.getGroupTo();
        int current = group.getMemberCount();
        if (current >= max) {
            throw new IllegalStateException("정원이 가득 찼습니다.");
        }

        // 멤버 생성 + 저장
        StudyGroupMember savedMember = studyGroupMemberRepository.save(
                StudyGroupMember.create(group, user)
        );

        // 인원 수 증가
        group.setMemberCount(current + 1);

        // 활동 로그 (JOIN)
        StudyGroupLog log = StudyGroupLog.of(
                group,
                StudyGroupActivityType.JOIN,
                user,                         // actor
<<<<<<< HEAD
                null,                         // target (자기 자신이지만 nullable 이라 null)
=======
                null,                         // target (자기 자신이지만 nullable 이라 null 처리)
>>>>>>> bc4f78f ([추가]스터디그룹 로그)
                StudyGroupRefEntityType.MEMBERSHIP,
                null,
                user.getName() + " 스터디 그룹에 가입"
        );
        studyGroupLogRepository.save(log);

        // capacity 정보
        StudyGroupCapacityDto capacityDto =
                new StudyGroupCapacityDto(max, current + 1, 0);

        // 응답
        return new StudyGroupJoinResponse(
                groupId,
                userId,
                "MEMBER",
                "JOINED",
                capacityDto,
                savedMember.getJoinedAt()
        );
    }

    /**
     * 내가 속한 그룹에서 탈퇴
     */
    public StudyGroupLeaveResponse leave(Long groupId, Long userId) {

        StudyGroupMember member = studyGroupMemberRepository
                .findByGroup_GroupIdAndMember_Id(groupId, userId)
                .orElseThrow(() -> new NoSuchElementException("가입 정보가 없습니다."));

        StudyGroup group = member.getGroup();
        User user = member.getMember();

        // 그룹장 탈퇴 방지
        if (group.getGroupLeader() != null && group.getGroupLeader().equals(userId)) {
            throw new IllegalStateException("그룹장은 먼저 권한을 위임하거나 그룹을 해체해야 탈퇴할 수 있습니다.");
        }

        // 멤버 삭제
        studyGroupMemberRepository.delete(member);

        // 인원 수 감소
        int current = group.getMemberCount();
        group.setMemberCount(Math.max(0, current - 1));

        // 활동 로그 (LEAVE)
        StudyGroupLog log = StudyGroupLog.of(
                group,
                StudyGroupActivityType.LEAVE,
                user,                         // actor
                null,                         // target
                StudyGroupRefEntityType.MEMBERSHIP,
                null,
                user.getName() + " 스터디 그룹에서 탈퇴"
        );
        studyGroupLogRepository.save(log);

        return new StudyGroupLeaveResponse(groupId, userId, "LEFT");
    }

    /**
     * 그룹장에 의한 멤버 강퇴
     */
    public StudyGroupKickResponse kick(Long groupId, Long actorUserId, Long targetUserId) {

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("StudyGroup not found: " + groupId));

        Long leaderId = group.getGroupLeader();
        if (leaderId == null || !leaderId.equals(actorUserId)) {
            throw new AccessDeniedException("그룹장만 멤버를 강퇴할 수 있습니다.");
        }

        // 그룹장이 자기 자신 강퇴 방지
        if (actorUserId.equals(targetUserId)) {
            throw new IllegalStateException("그룹장은 자기 자신을 강퇴할 수 없습니다. 탈퇴 기능을 사용해주세요.");
        }

        // 강퇴 대상 멤버 조회
        StudyGroupMember targetMember = studyGroupMemberRepository
                .findByGroup_GroupIdAndMember_Id(groupId, targetUserId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자는 이 그룹의 멤버가 아닙니다."));

        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new NoSuchElementException("Actor user not found: " + actorUserId));

        User target = targetMember.getMember();

        // 멤버 삭제
        studyGroupMemberRepository.delete(targetMember);

        // 인원 수 감소
        int current = group.getMemberCount();
        group.setMemberCount(Math.max(0, current - 1));

        // 활동 로그 (KICK)
        StudyGroupLog log = StudyGroupLog.of(
                group,
                StudyGroupActivityType.KICK,
                actor,                        // actor (그룹장)
                target,                       // target (강퇴된 멤버)
                StudyGroupRefEntityType.MEMBERSHIP,
                null,
                target.getName() + " 멤버 강퇴"
        );
        studyGroupLogRepository.save(log);

        return new StudyGroupKickResponse(
                groupId,
                target.getId(),
                target.getName(),
                "KICKED"
        );
    }

    /**
     * 활동 로그 조회 (페이지네이션)
<<<<<<< HEAD
     */
    public StudyGroupActivityPageResponse getActivities(Long groupId, int page, int size) {

        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, size);

        Page<StudyGroupLog> logPage =
                studyGroupLogRepository.findByGroup_GroupIdOrderByCreatedAtDesc(groupId, pageable);

        List<StudyGroupActivityItemResponse> content = logPage.getContent().stream()
                .map(StudyGroupActivityItemResponse::fromEntity)
                .collect(Collectors.toList());

        StudyGroupActivityPageResponse response = new StudyGroupActivityPageResponse();
        response.setContent(content);
        response.setPage(page);                         // 1-base 페이지 번호
        response.setSize(size);
        response.setTotalPages(logPage.getTotalPages());
        response.setTotalElements(logPage.getTotalElements());

        return response;
    }
=======
     *//**
 * 활동 로그 조회 (페이지네이션)
 */
@Transactional
public StudyGroupActivityPageResponse getActivities(Long groupId, int page, int size) {

    int pageIndex = Math.max(0, page - 1);
    Pageable pageable = PageRequest.of(pageIndex, size);

    Page<StudyGroupLog> logPage =
            studyGroupLogRepository.findByGroup_GroupIdOrderByCreatedAtDesc(groupId, pageable);

    List<StudyGroupActivityItemResponse> content = logPage.getContent().stream()
            .map(StudyGroupActivityItemResponse::fromEntity)
            .collect(Collectors.toList());

    // 🔥 여기서부터: 생성자에 값 안 넣고, 기본 생성자 + setter 로 채우기
    StudyGroupActivityPageResponse response = new StudyGroupActivityPageResponse();
    response.setContent(content);
    response.setPage(page);                         // 1-base 페이지 번호
    response.setSize(size);
    response.setTotalPages(logPage.getTotalPages());
    response.setTotalElements(logPage.getTotalElements());

    return response;
}

>>>>>>> bc4f78f ([추가]스터디그룹 로그)
}
