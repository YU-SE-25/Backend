package com.unide.backend.domain.discuss.entity;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dis_comment_report") // ✅ 방금 만든 테이블명과 동일
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussCommentReport {

    // ✅ PK = report_id (reports.id 그대로 사용, 자동 생성 아님)
    @Id
    @Column(name = "report_id")
    private Long reportId;

    // ✅ 공용 reports 테이블과 연관 (같은 컬럼 공유, 읽기 전용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private Report report;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // 신고한 댓글 (FK → discuss_comment.comment_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private DiscussComment comment;

    @Column(nullable = false, length = 100)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscussCommentReportStatus status;

    @Column(name = "report_at", nullable = false)
    private LocalDateTime reportAt;

    @PrePersist
    protected void onCreate() {
        if (reportAt == null) {
            reportAt = LocalDateTime.now();
        }
        if (status == null) {
            status = DiscussCommentReportStatus.UNPROCESS;
        }
    }
}
