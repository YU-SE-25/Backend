// submissions 테이블과 매핑되는 엔터티

package com.unide.backend.domain.submissions.entity;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "submissions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problems problem;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    private Integer runtime;
    private Integer memory;

    @Column(nullable = false)
    private boolean isShared;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String compileOutput;

    private Integer compileTimeMs;
    private Integer currentTestCase;
    private Integer passedTestCases;
    private Integer totalTestCases;

    @Builder
    public Submissions(User user, Problems problem, String code, SubmissionLanguage language, SubmissionStatus status, boolean isShared, Integer totalTestCases) {
        this.user = user;
        this.problem = problem;
        this.code = code;
        this.language = language;
        this.status = (status != null) ? status : SubmissionStatus.PENDING;
        this.isShared = isShared;
        this.totalTestCases = totalTestCases;
        this.submittedAt = LocalDateTime.now();
        this.passedTestCases = 0;
    }

    public void updateDraft(String code, SubmissionLanguage language) {
        this.code = code;
        this.language = language;
        this.submittedAt = LocalDateTime.now();
    }

    public void updateResult(SubmissionStatus status, Integer runtime, Integer memory, Integer passedTestCases, String compileOutput) {
        this.status = status;
        this.runtime = runtime;
        this.memory = memory;
        this.passedTestCases = passedTestCases;
        this.compileOutput = compileOutput;
    }

    public void updateShareStatus(boolean isShared) {
        this.isShared = isShared;
    }
}
