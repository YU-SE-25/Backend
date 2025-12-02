package com.unide.backend.domain.mypage.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_goals")
public class Goals extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private Integer dailyMinimumStudyMinutes;
    private Integer weeklyStudyGoalMinutes;

    @Column(columnDefinition = "JSON")
    private String studyTimeByLanguage;

    @Builder
    public Goals(User user,
                 Integer dailyMinimumStudyMinutes,
                 Integer weeklyStudyGoalMinutes,
                 String reminderTimes,
                 Boolean isReminderEnabled,
                 String studyTimeByLanguage) {
        this.user = user;
        this.dailyMinimumStudyMinutes = dailyMinimumStudyMinutes;
        this.weeklyStudyGoalMinutes = weeklyStudyGoalMinutes;
        this.studyTimeByLanguage = studyTimeByLanguage;
    }

    public void updateDailyMinimumStudyMinutes(Integer v) { this.dailyMinimumStudyMinutes = v; }
    public void updateWeeklyStudyGoalMinutes(Integer v) { this.weeklyStudyGoalMinutes = v; }
    public void updateStudyTimeByLanguage(String json) { this.studyTimeByLanguage = json; }
}
