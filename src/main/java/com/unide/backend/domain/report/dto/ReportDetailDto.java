package com.unide.backend.domain.report.dto;

import java.time.LocalDateTime;

import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.entity.ReportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDetailDto {

    private Long id;

    // reporter info
    private Long reporterId;
    private String reporterName;

    // target info
    private Long targetId;
    private String targetName;

    private ReportType type;

    private String reason;
    private ReportStatus status;

    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
}