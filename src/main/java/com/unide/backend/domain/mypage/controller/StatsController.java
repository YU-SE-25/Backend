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

    /**
     * 주간 평판 변화량 리스트 조회 (내림차순)
     */
    @GetMapping("/weekly-rating-delta-list")
    public ResponseEntity<java.util.List<StatsService.WeeklyRatingDeltaDto>> getWeeklyRatingDeltaList() {
        return ResponseEntity.ok(statsService.getWeeklyRatingDeltaList());
    }

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
