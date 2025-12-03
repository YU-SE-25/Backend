package com.unide.backend.domain.mypage.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    private UserGoalsRequestDto userGoals;
    private List<ReminderRequestDto> reminders;
    private Boolean isDarkMode;
    private Boolean isStudyAlarm;
    private MultipartFile avatarImageFile;
}