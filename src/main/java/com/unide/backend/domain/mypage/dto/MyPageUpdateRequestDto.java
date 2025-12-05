package com.unide.backend.domain.mypage.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MyPageUpdateRequestDto {
    private String nickname;
    private String bio;
    private List<String> preferredLanguage;
    private Boolean isPublic;
    private Boolean isDarkMode;
    private Boolean isStudyAlarm;

    private UserGoalsRequestDto userGoals;
    private List<ReminderRequestDto> reminders;

    private String avatarUrl;
}