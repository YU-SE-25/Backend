package com.unide.backend.domain.mainpage.entity;

import java.time.LocalDateTime;

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
@Table(name = "reputation_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReputationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Integer ranking;

    private Integer delta;

    @Builder
    public ReputationEvent(User user, Integer ranking, Integer delta) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.ranking = ranking;
        this.delta = delta;
    }
}
