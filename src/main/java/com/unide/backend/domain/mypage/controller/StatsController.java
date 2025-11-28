package com.unide.backend.domain.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.unide.backend.domain.mypage.dto.UserStatsResponseDto;
import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /** 내 통계 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserStatsResponseDto> getStats(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUser().getId();
        return ResponseEntity.ok(statsService.getStats(userId));
    }

    // /** 내 통계 업데이트 */
    // @PatchMapping("/me")
    // public ResponseEntity<Void> updateStats(
    //         @AuthenticationPrincipal PrincipalDetails principal,
    //         @RequestBody UserStatsResponseDto dto
    // ) {
    //     Long userId = principal.getUser().getId();
    //     statsService.updateStats(userId);
    //     return ResponseEntity.ok().build();
    // }
}
