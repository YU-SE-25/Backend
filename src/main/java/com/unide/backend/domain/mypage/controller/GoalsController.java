package com.unide.backend.domain.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.unide.backend.domain.mypage.dto.MyPageUpdateResponseDto;
import com.unide.backend.domain.mypage.dto.UserGoalsResponseDto;
import com.unide.backend.domain.mypage.service.GoalsService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mypage/goals")
@RequiredArgsConstructor
public class GoalsController {

    private final GoalsService goalsService;

    /** 내 목표 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserGoalsResponseDto> getGoals(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUser().getId();
        return ResponseEntity.ok(goalsService.getGoals(userId));
    }

    /** 내 목표 수정 */
    @PatchMapping("/me")
    public ResponseEntity<MyPageUpdateResponseDto> updateGoals(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody UserGoalsResponseDto dto
    ) {
        Long userId = principal.getUser().getId();
        goalsService.updateGoals(userId, dto);
        LocalDateTime updatedAt = LocalDateTime.now();
        return ResponseEntity.ok(new MyPageUpdateResponseDto("목표가 성공적으로 수정되었습니다.", updatedAt));
    }
}
