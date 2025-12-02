package com.unide.backend.domain.mypage.entity;

import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reminder")
public class Reminder{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int day; // 1~7 (1=월요일, 7=일요일)

    @Column(columnDefinition = "JSON")
    private String times; // ["08:00", "21:00"] 등 JSON 문자열

    @Builder
    public Reminder(User user, int day, String times) {
        this.user = user;
        this.day = day;
        this.times = times;
    }

    public void updateDay(int day) {
        this.day = day;
    }

    public void updateTimes(String times) {
        this.times = times;
    }
}
