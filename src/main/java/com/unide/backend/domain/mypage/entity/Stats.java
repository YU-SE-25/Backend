package com.unide.backend.domain.mypage.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

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
@Table(name = "user_stats")
public class Stats extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private Integer totalSolved;
    private Integer totalSubmitted;
    private Double acceptanceRate;
    private Integer streakDays;
    private Integer ranking;
    private Integer rating;

    @Builder
    public Stats(User user, Integer totalSolved, Integer totalSubmitted,
                 Double acceptanceRate, Integer streakDays, Integer ranking, Integer rating) {
        this.user = user;
        this.totalSolved = totalSolved;
        this.totalSubmitted = totalSubmitted;
        this.acceptanceRate = acceptanceRate;
        this.streakDays = streakDays;
        this.ranking = ranking;
        this.rating = rating;
    }

    public void updateTotalSolved(Integer v) { this.totalSolved = v; }
    public void updateTotalSubmitted(Integer v) { this.totalSubmitted = v; }
    public void updateAcceptanceRate(Double v) { this.acceptanceRate = v; }
    public void updateStreakDays(Integer v) { this.streakDays = v; }
    public void updateRanking(Integer v) { this.ranking = v; }
    public void updateRating(Integer v) { this.rating = v; }
}
