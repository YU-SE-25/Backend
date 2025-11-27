package com.unide.backend.domain.studygroup.member.controller;

import com.unide.backend.domain.studygroup.member.dto.StudyGroupJoinResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupLeaveResponse;
import com.unide.backend.domain.studygroup.member.dto.StudyGroupKickResponse;
import com.unide.backend.domain.studygroup.member.service.StudyGroupMemberService;
import com.unide.backend.global.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
