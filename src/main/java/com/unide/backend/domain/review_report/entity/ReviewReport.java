package com.unide.backend.domain.review_report.entity;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReport {

    @Id
    @Column(name = "report_id")
    private Long reportId;

    /**
     * Report와 PK를 공유하는 1:1 매핑
     * Report의 id가 곧 이 엔티티의 PK가 됨.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CodeReview post;

    @Column(nullable = false, length = 100)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewReportStatus status;

    @Column(name = "report_at", nullable = false)
    private LocalDateTime reportAt;

    @PrePersist
    protected void onCreate() {
        if (reportAt == null) {
            reportAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ReviewReportStatus.UNPROCESS;
        }
    }
}
