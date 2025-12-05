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
public class ReportListDto {
    private Long id;
    private String reporterName;
    private String targetName;
    private ReportType type;
    private ReportStatus status;
    private LocalDateTime reportedAt;
}

