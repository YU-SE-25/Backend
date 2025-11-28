package com.unide.backend.domain.mypage.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserGoalsResponseDto {
    private final Map<String, Integer> studyTimeByLanguage; // 언어별 학습 시간
    private final Integer dailyMinimumStudyMinutes;         // 하루 최소 학습 시간
    private final Integer weeklyStudyGoalMinutes;           // 주간 목표 시간
    private final String[] reminderTimes;                   // 알림 시간 배열 ( "18:00" 이런 형식)
    private final Boolean isReminderEnabled;                // 알림 활성화 여부
}
