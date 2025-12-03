package com.unide.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
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
    private int previousRanking; // 이전 순위
    private int delta;     // 랭킹 변화량  ← 추가!
    private double score; // 복합 점수 (순위 계산용)
}
