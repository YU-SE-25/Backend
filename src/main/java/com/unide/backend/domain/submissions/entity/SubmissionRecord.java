package com.unide.backend.domain.submissions.entity;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "submission_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submissions submission; // 부모 제출 정보

    @Column(nullable = false)
    private int testCaseIndex; // 몇 번째 테스트 케이스인지 (1, 2, 3...)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status; // 해당 케이스의 결과 (CA, TLE 등)

    @Column(nullable = false)
    private int runtime; // 실행 시간 (ms)

    @Column(nullable = false)
    private int memory; // 메모리 사용량 (KB)

    @Builder
    public SubmissionRecord(Submissions submission, int testCaseIndex, SubmissionStatus status, int runtime, int memory) {
        this.submission = submission;
        this.testCaseIndex = testCaseIndex;
        this.status = status;
        this.runtime = runtime;
        this.memory = memory;
    }
}
