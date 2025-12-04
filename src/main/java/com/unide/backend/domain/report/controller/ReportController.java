package com.unide.backend.domain.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.unide.backend.domain.report.dto.ReportCreateRequestDto;
import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.service.ReportService;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.report.dto.ReportResolveRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /**
     * 신고 생성
     */
    @PostMapping("/register")
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

    /** 모든 신고 리스트 조회 (관리자용) */
    @GetMapping
    public ResponseEntity<List<ReportListDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    /** 제목으로 신고 리스트 검색 (관리자용) */
    @GetMapping("/search")
    public ResponseEntity<List<ReportListDto>> searchReportsByTitle(@RequestParam String keyword) {
        return ResponseEntity.ok(reportService.searchReportsByTitle(keyword));
    }

    /** 신고 처리(관리자용) - 상태, 액션, 메모 */
    @PatchMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(
            @PathVariable Long reportId,
            @RequestBody ReportResolveRequestDto dto
    ) {
        reportService.resolveReport(reportId, dto);
        return ResponseEntity.ok().build();
    }
}
