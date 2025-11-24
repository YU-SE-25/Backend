package com.unide.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStats {
    private final int totalSolved;
    private final int totalSubmitted;
    private final double acceptanceRate;
    private final int streakDays;
    private final int rank;
    private final int rating;
}
