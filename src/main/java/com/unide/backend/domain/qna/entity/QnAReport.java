package com.unide.backend.domain.qna.entity;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qna_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnAReport {

    // PK = report_id (reports.id 그대로 사용, 자동 생성 X)
    @Id
    @Column(name = "report_id")
    private Long reportId;

    // 공용 reports 테이블과 연관 (같은 컬럼 공유, 읽기 전용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private QnA post;

    @Column(nullable = false, length = 100)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QnAReportStatus status;

    @Column(name = "report_at", nullable = false)
    private LocalDateTime reportAt;

    @PrePersist
    protected void onCreate() {
        if (reportAt == null) {
            reportAt = LocalDateTime.now();
        }
        if (status == null) {
            status = QnAReportStatus.UNPROCESS;
        }
    }
}
