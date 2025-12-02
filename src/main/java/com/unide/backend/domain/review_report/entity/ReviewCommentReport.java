package com.unide.backend.domain.review_report.entity;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.entity.CodeReviewComment;
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
@Table(name = "code_review_comment_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentReport {

    @Id
    @Column(name = "report_id")
    private Long reportId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CodeReviewComment reviewComment;

    // 🔹 새로 추가: 신고 댓글이 달린 원본 리뷰(post_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CodeReview post;

    @Column(nullable = false, length = 100)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(name = "report_at", nullable = false)
    private LocalDateTime reportAt;

    @PrePersist
    protected void onCreate() {
        if (reportAt == null) {
            reportAt = LocalDateTime.now();
        }
        if (status == null) {
            status =ReportStatus.PENDING;
        }
    }
}
