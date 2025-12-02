package com.unide.backend.domain.analysis.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_analysis_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAnalysisReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(nullable = false)
    private String summary; // 종합 요약

    @Lob
    @Column(nullable = false)
    private String strengths; // 장점 (JSON Array 문자열로 저장)

    @Lob
    @Column(nullable = false)
    private String weaknesses; // 단점/개선점 (JSON Array 문자열로 저장)

    @Lob
    @Column(nullable = false)
    private String suggestions; // 추천 학습 키워드 (JSON Array 문자열로 저장)

    @Builder
    public UserAnalysisReport(User user, String summary, String strengths, String weaknesses, String suggestions) {
        this.user = user;
        this.summary = summary;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
    }
}
