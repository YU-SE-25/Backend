package com.unide.backend.domain.qna.entity;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "qna_comment_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnACommentReport {

    @Id
    @Column(name = "report_id")
    private Long reportId;   // PK = Report.id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private QnAComment comment;

    @Column(nullable = false, length = 100)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(name = "report_at", nullable = false)
    private LocalDateTime reportAt;

    // ===== 수동 Setter (insertable=false/updatable=false 때문에 명시적으로 정의하는 게 안전함) =====

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public void setComment(QnAComment comment) {
        this.comment = comment;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public void setReportAt(LocalDateTime reportAt) {
        this.reportAt = reportAt;
    }

    // ===== 자동 생성 =====
    @PrePersist
    protected void onCreate() {
        if (reportAt == null) reportAt = LocalDateTime.now();
        if (status == null) status = ReportStatus.PENDING;
    }
}
