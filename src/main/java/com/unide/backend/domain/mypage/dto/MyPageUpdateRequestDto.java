package com.unide.backend.domain.mypage.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.unide.backend.domain.mypage.dto.ReminderRequestDto;
import com.unide.backend.domain.mypage.dto.UserGoalsRequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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