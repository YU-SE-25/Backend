package com.unide.backend.domain.report.dto;

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

public class ReportCreateRequestDto {

    private Long targetId;          // 신고 대상 ID (유저 or 문제)
    private ReportType type;        // USER / PROBLEM
    private String reason;          // 신고 사유
}

