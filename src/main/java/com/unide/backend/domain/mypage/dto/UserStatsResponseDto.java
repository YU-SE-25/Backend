package com.unide.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponseDto {

    private Long userId;
    private String nickname;      // ⭐ 닉네임 추가

    private int totalSolved;
    private int totalSubmitted;
    private double acceptanceRate;
    private int streakDays;

    private int ranking;          // 현재 순위
    private int rating;           // 평판 점수
    private int previousRanking;  // 이전 순위
    private int delta;            // 순위 변화량
    private double score;         // 복합 점수
}
