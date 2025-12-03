package com.unide.backend.domain.mypage.dto;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGoalsRequestDto {
    private Map<String, Integer> studyTimeByLanguage; // 언어별 학습 시간
    private Integer dailyMinimumStudyMinutes;         // 하루 최소 학습 시간
    private Integer weeklyStudyGoalMinutes;           // 주간 목표 시간
}
