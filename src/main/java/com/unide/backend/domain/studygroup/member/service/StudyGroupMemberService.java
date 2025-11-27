package com.unide.backend.domain.studygroup.member.service;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupRepository;
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
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyGroupMemberService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final StudyGroupLogRepository studyGroupLogRepository;
    private final UserRepository userRepository;

    /**
     * 스터디 그룹 가입
     */
    @Transactional
    public StudyGroupJoinResponse join(Long groupId, Long userId) {
        // 그룹 조회
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("StudyGroup not found: " + groupId));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        // 이미 가입되어 있는지 체크
        if (studyGroupMemberRepository.existsByGroup_GroupIdAndMember_Id(groupId, userId)) {
            throw new IllegalStateException("이미 가입된 스터디 그룹입니다.");
        }

        // 인원 체크
        int max = group.getGroupTo();          // group_TO
        int current = group.getMemberCount();  // member_count

        if (current >= max) {
            throw new IllegalStateException("정원이 가득 찼습니다.");
        }

        // 멤버 생성 + 저장 (save 반환값 사용해서 joinedAt 포함된 객체 받기)
        StudyGroupMember savedMember = studyGroupMemberRepository.save(
                StudyGroupMember.create(group, user)
        );

        // 그룹 인원 수 증가
        group.setMemberCount(current + 1);

        // 활동 로그 (JOIN)
        StudyGroupLog log = StudyGroupLog.of(
                group,
                StudyGroupActivityType.JOIN,
                user,                        // actor
                user,                        // target
                StudyGroupRefEntityType.MEMBERSHIP,
                null,                        // refEntityId (멤버십 PK 없으니 null)
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
                savedMember.getJoinedAt()    // ✅ 이제 null 안 나옴
        );
    }

    /**
     * 내가 속한 그룹에서 탈퇴
     */
    @Transactional
    public StudyGroupLeaveResponse leave(Long groupId, Long userId) {
        StudyGroupMember member = studyGroupMemberRepository
                .findByGroup_GroupIdAndMember_Id(groupId, userId)
                .orElseThrow(() -> new NoSuchElementException("가입 정보가 없습니다."));

        StudyGroup group = member.getGroup();
        User user = member.getMember();

        // 멤버 삭제
        studyGroupMemberRepository.delete(member);

        // 인원 수 감소 (0 밑으로는 내려가지 않도록)
        int current = group.getMemberCount();
        group.setMemberCount(Math.max(0, current - 1));

        // 활동 로그 (LEAVE)
        StudyGroupLog log = StudyGroupLog.of(
                group,
                StudyGroupActivityType.LEAVE,
                user,                        // actor
                user,                        // target
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
    @Transactional
    public StudyGroupKickResponse kick(Long groupId, Long actorUserId, Long targetUserId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("StudyGroup not found: " + groupId));

        // 그룹장 권한 체크
        Long leaderId = group.getGroupLeader();  // group_leader 컬럼(Long userId)
        if (leaderId == null || !leaderId.equals(actorUserId)) {
            throw new AccessDeniedException("그룹장만 멤버를 강퇴할 수 있습니다.");
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
                actor,                       // actor
                target,                      // target
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
}
