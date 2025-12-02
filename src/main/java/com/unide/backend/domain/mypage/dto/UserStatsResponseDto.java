package com.unide.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponseDto {
     private Long userId;

    private int totalSolved;
    private int totalSubmitted;
    private double acceptanceRate;
    private int streakDays;

    private int ranking;   // 순위
    private int rating;    // 평판 점수
    private int delta;     // 랭킹 변화량  ← 추가!
}
