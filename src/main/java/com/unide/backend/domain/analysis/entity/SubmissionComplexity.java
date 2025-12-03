package com.unide.backend.domain.analysis.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.submissions.entity.Submissions;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "submission_complexity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionComplexity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submissions submission;

    @Column(nullable = false, length = 50)
    private String timeComplexity; // 예: O(N)

    @Column(nullable = false)
    private String timeReason; // 추정 이유

    @Column(nullable = false, length = 50)
    private String spaceComplexity; // 예: O(1)

    @Column(nullable = false)
    private String spaceReason; // 추정 이유

    @Builder
    public SubmissionComplexity(Submissions submission, String timeComplexity, String timeReason, String spaceComplexity, String spaceReason) {
        this.submission = submission;
        this.timeComplexity = timeComplexity;
        this.timeReason = timeReason;
        this.spaceComplexity = spaceComplexity;
        this.spaceReason = spaceReason;
    }
}
