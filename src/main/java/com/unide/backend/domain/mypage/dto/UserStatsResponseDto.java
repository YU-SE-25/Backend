package com.unide.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserStatsResponseDto {
    private final int totalSolved;
    private final int totalSubmitted;
    private final double acceptanceRate;
    private final int streakDays;
    private final int ranking;
    private final int rating;
}

