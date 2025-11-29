package com.unide.backend.domain.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.admin.service.AdminPageService;
import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.dto.ReportResolveRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/page")
public class AdminPageController {
    private final AdminPageService adminPageService;

    /** 신고 목록 조회 */
    @GetMapping("/reports")
    public ResponseEntity<List<ReportListDto>> getReportList() {
        List<ReportListDto> reports = adminPageService.getReportList();
        return ResponseEntity.ok(reports);
    }
    /** 신고 상세 조회 */
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportDetailDto> getReportDetail(@PathVariable Long reportId)
    {
        ReportDetailDto reportDetail = adminPageService.getReportDetail(reportId);
        return ResponseEntity.ok(reportDetail);
    }
    /** 신고 처리 */
    @PostMapping("/reports/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(
            @PathVariable Long reportId,
            @RequestBody ReportResolveRequestDto requestDto) {
        adminPageService.resolveReport(reportId, requestDto);
        return ResponseEntity.ok().build();
    }
}