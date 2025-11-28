package com.unide.backend.domain.mypage.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unide.backend.domain.mypage.dto.UserGoalsResponseDto;
import com.unide.backend.domain.mypage.entity.Goals;
import com.unide.backend.domain.mypage.repository.GoalsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalsService {

    private final GoalsRepository goalsRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /** 목표 조회 */
    public UserGoalsResponseDto getGoals(Long userId) {
        Goals goals = goalsRepository.findByUserId(userId);

        if (goals == null) {
            return UserGoalsResponseDto.builder()
                    .dailyMinimumStudyMinutes(0)
                    .weeklyStudyGoalMinutes(0)
                    .reminderTimes(new String[]{})
                    .isReminderEnabled(false)
                    .studyTimeByLanguage(null)
                    .build();
        }

        Map<String, Integer> studyTimeMap = null;
        try {
            if (goals.getStudyTimeByLanguage() != null) {
                studyTimeMap = objectMapper.readValue(
                        goals.getStudyTimeByLanguage(),
                        objectMapper.getTypeFactory()
                                .constructMapType(Map.class, String.class, Integer.class)
                );
            }
        } catch (Exception ignored) {}

        return UserGoalsResponseDto.builder()
                .dailyMinimumStudyMinutes(goals.getDailyMinimumStudyMinutes())
                .weeklyStudyGoalMinutes(goals.getWeeklyStudyGoalMinutes())
                .reminderTimes(goals.getReminderTimes().split(","))
                .isReminderEnabled(goals.getIsReminderEnabled())
                .studyTimeByLanguage(studyTimeMap)
                .build();
    }

    /** 목표 수정 */
    @Transactional
    public void updateGoals(Long userId, UserGoalsResponseDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Goals goals = goalsRepository.findByUserId(userId);

        String studyTimeJson = null;
        try {
            if (dto.getStudyTimeByLanguage() != null)
                studyTimeJson = objectMapper.writeValueAsString(dto.getStudyTimeByLanguage());
        } catch (JsonProcessingException ignored) {}

        if (goals == null) {
            goals = Goals.builder()
                    .user(user)
                    .dailyMinimumStudyMinutes(dto.getDailyMinimumStudyMinutes())
                    .weeklyStudyGoalMinutes(dto.getWeeklyStudyGoalMinutes())
                    .reminderTimes(String.join(",", dto.getReminderTimes()))
                    .isReminderEnabled(dto.getIsReminderEnabled())
                    .studyTimeByLanguage(studyTimeJson)
                    .build();
        } else {
            goals.updateDailyMinimumStudyMinutes(dto.getDailyMinimumStudyMinutes());
            goals.updateWeeklyStudyGoalMinutes(dto.getWeeklyStudyGoalMinutes());
            goals.updateReminderTimes(String.join(",", dto.getReminderTimes()));
            goals.updateIsReminderEnabled(dto.getIsReminderEnabled());
            goals.updateStudyTimeByLanguage(studyTimeJson);
        }

        goalsRepository.save(goals);
    }
}
