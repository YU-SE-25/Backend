package com.unide.backend.domain.qna.entity;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qna_comment_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnACommentReport {

    @Id
    @Column(name = "report_id")
    private Long reportId;

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
    private QnACommentReportStatus status;

    @Column(name = "report_at", nullable = false)
    private LocalDateTime reportAt;

    @PrePersist
    protected void onCreate() {
        if (reportAt == null) {
            reportAt = LocalDateTime.now();
        }
        if (status == null) {
            status = QnACommentReportStatus.UNPROCESS;
        }
    }
}
