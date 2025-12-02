package com.unide.backend.domain.report.dto;

import com.unide.backend.domain.report.entity.ReportStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportHandleRequestDto {
    private ReportStatus status; // 처리 상태 (APPROVED, REJECTED 등)
    private String reason;       // 처리 사유 (관리자가 입력)
}