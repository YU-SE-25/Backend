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
@Table(name = "submission_flowchart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionFlowchart extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submissions submission;

    @Lob
    @Column(nullable = false)
    private String mermaidCode; // Mermaid.js 텍스트 코드

    @Builder
    public SubmissionFlowchart(Submissions submission, String mermaidCode) {
        this.submission = submission;
        this.mermaidCode = mermaidCode;
    }
}
