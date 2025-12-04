package com.unide.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private int previousRating; // 이전 평판 점수
    private int ratingDelta; // 평판 점수 변화량
    private int weeklyRatingDelta; // 주간 평판 변화량
}
