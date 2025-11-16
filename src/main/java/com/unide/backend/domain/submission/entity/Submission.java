package com.unide.backend.domain.submission.entity;

import java.time.LocalDateTime;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.problem.entity.Problem;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "submission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(columnDefinition = "LONGTEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgrammingLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(name = "runtime_ms")
    private Integer runtimeMs;

    @Column
    private Integer memory;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Builder
    public Submission(User user, Problem problem, String code, ProgrammingLanguage language) {
        this.user = user;
        this.problem = problem;
        this.code = code;
        this.language = language;
        this.status = SubmissionStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
    }

    public void updateStatus(SubmissionStatus status) {
        this.status = status;
    }

    public void updateResult(Integer runtimeMs, Integer memory) {
        this.runtimeMs = runtimeMs;
        this.memory = memory;
    }
}
