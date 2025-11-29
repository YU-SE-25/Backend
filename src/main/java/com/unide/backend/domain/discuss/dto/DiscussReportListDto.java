package com.unide.backend.domain.discuss.dto;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussReportListDto {

    private Long id;
    private String reporterName;
    private String targetSummary;     // 게시글 제목 or 댓글 일부
    private String reason;
    private String targetType;        // "POST" or "COMMENT"
    private ReportStatus status;
    private LocalDateTime reportedAt;
}
