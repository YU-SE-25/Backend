package com.unide.backend.domain.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import com.unide.backend.domain.report.dto.ReportCreateRequestDto;
import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.service.ReportService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    /** 신고 승인 (관리자만) */
    @PutMapping("/{reportId}/approve")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> approveReport(@PathVariable Long reportId) {
        reportService.updateReportStatus(reportId, com.unide.backend.domain.report.entity.ReportStatus.APPROVED);
        return ResponseEntity.ok("신고가 승인되었습니다.");
    }

    /** 신고 거절 (관리자만) */
    @PutMapping("/{reportId}/reject")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> rejectReport(@PathVariable Long reportId) {
        reportService.updateReportStatus(reportId, com.unide.backend.domain.report.entity.ReportStatus.REJECTED);
        return ResponseEntity.ok("신고가 반려되었습니다.");
    }

    /**
     * 신고 상태 변경 (승인/거절 등)
     * 예시: PATCH /api/reports/{reportId}/status
     * body: { "status": "APPROVED" } 또는 { "status": "REJECTED" }
     */
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable Long reportId,
            @RequestBody StatusUpdateRequest request
    ) {
        reportService.updateReportStatus(reportId, request.getStatus());
        return ResponseEntity.ok("신고 상태가 변경되었습니다.");
    }

    /** 신고 상태 변경 요청 DTO */
    @lombok.Getter
    @lombok.NoArgsConstructor
    public static class StatusUpdateRequest {
        private com.unide.backend.domain.report.entity.ReportStatus status;
    }

    private final ReportService reportService;

    /**
     * 신고 생성
     */
    @PostMapping
    public ResponseEntity<?> createReport(
            @AuthenticationPrincipal PrincipalDetails user,
            @RequestBody ReportCreateRequestDto request
    ) {
        reportService.createReport(user.getUser().getId(), request);
        return ResponseEntity.ok("신고가 접수되었습니다.");
    }

    /**
     * 내가 한 신고 리스트 조회
     */
    @GetMapping("/me")
    public ResponseEntity<List<ReportListDto>> getMyReports(
            @AuthenticationPrincipal PrincipalDetails user
    ) {
        return ResponseEntity.ok(reportService.getMyReports(user.getUser().getId()));
    }

    /**
     * 내가 한 특정 신고 상세 조회 (optional)
     */
    @GetMapping("/me/{reportId}")
    public ResponseEntity<ReportDetailDto> getMyReportDetail(
            @AuthenticationPrincipal PrincipalDetails user,
            @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(reportService.getMyReportDetail(user.getUser().getId(), reportId));
    }
}
