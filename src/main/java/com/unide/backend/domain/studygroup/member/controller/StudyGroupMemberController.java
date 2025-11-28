package com.unide.backend.domain.studygroup.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.studygroup.member.dto.StudyGroupActivityPageResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupJoinResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupKickResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupLeaveResponse;
import com.unide.backend.domain.studygroup.member.service.StudyGroupMemberService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studygroup")
public class StudyGroupMemberController {

    private final StudyGroupMemberService studyGroupMemberService;

    // ===== 그룹 가입 =====
    // POST /api/studygroup/{groupId}/membership
    @PostMapping("/{groupId}/membership")
    public StudyGroupJoinResponse join(
            @PathVariable Long groupId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUser().getId();
        return studyGroupMemberService.join(groupId, userId);
    }

    // ===== 내 탈퇴 =====
    // DELETE /api/studygroup/{groupId}/members/me
    @DeleteMapping("/{groupId}/members/me")
    public StudyGroupLeaveResponse leave(
            @PathVariable Long groupId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUser().getId();
        return studyGroupMemberService.leave(groupId, userId);
    }

    // ===== 멤버 강퇴 (그룹장만) =====
    // DELETE /api/studygroup/{groupId}/members/{targetUserId}
    @DeleteMapping("/{groupId}/members/{targetUserId}")
    public StudyGroupKickResponse kick(
            @PathVariable Long groupId,
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long actorUserId = principal.getUser().getId();
        return studyGroupMemberService.kick(groupId, actorUserId, targetUserId);
    }
    
    // ===== 활동 로그 조회 =====
    // GET /api/studygroup/{groupId}/activities?page=1&size=20
    @GetMapping("/{groupId}/activities")
    public StudyGroupActivityPageResponse getActivities(
            @PathVariable Long groupId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return studyGroupMemberService.getActivities(groupId, page, size);
    }
}
