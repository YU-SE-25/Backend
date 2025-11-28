package com.unide.backend.domain.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportResolveRequestDto;
import com.unide.backend.domain.admin.service.AdminPageService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPageController {
    private final AdminPageService adminPageService;

    /**
     * 신고 목록 조회
     */
    @GetMapping("/reports")
    public ResponseEntity<List<ReportListDto>> getReportList() {
        List<ReportListDto> list = adminPageService.getReportList();
        return ResponseEntity.ok(list);
    }

    /**
     * 신고 상세 조회
     */
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportDetailDto> getReportDetail(
            @PathVariable Long reportId
    ) {
        ReportDetailDto dto = adminPageService.getReportDetail(reportId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 신고 처리 (승인, 반려)
     */
    @PostMapping("/reports/{reportId}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long reportId,
            @RequestBody ReportResolveRequestDto request
    ) {
        adminPageService.resolveReport(reportId, request);
        return ResponseEntity.ok().body("신고 처리가 완료되었습니다.");
    }

    
}
