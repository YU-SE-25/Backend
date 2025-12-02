package com.unide.backend.domain.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.dto.ReportResolveRequestDto;
import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.report.service.ReportService;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPageService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportService reportService;   // 🔹 추가

    // =========================================
    // 🔥 신고 관리 기능 (Reports)
    // =========================================

    public List<ReportListDto> getReportList() {
        return reportRepository.findAll().stream()
                .map(this::toReportListDto)
                .toList();
    }

    public ReportDetailDto getReportDetail(Long id) {
        Report report = findReport(id);
        return toReportDetailDto(report);
    }

    @Transactional
    public ReportDetailDto approveReport(Long id) {
        Report report = findReport(id);
        report.setStatus(ReportStatus.APPROVED);
        return toReportDetailDto(reportRepository.save(report));
    }

    @Transactional
    public ReportDetailDto resolveReport(Long id, ReportResolveRequestDto dto) {

        // 1) 공통 서비스에 위임 (APPROVED / REJECTED 처리 + 평판 반영 + 메일)
        reportService.updateReportStatus(id, dto.getStatus());

        // 2) 다시 조회해서 DTO 변환
        Report report = findReport(id);
        return toReportDetailDto(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    // =========================================
    // 🔥 내부 공통 함수들
    // =========================================

    private Report findReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신고 정보를 찾을 수 없습니다."));
    }


    // =========================================
    // 🔥 DTO 변환 메서드
    // =========================================

    private ReportListDto toReportListDto(Report r) {

        String reporterName = getUserName(r.getReporterId());
        String targetName = getUserName(r.getTargetId());

        return ReportListDto.builder()
                .id(r.getId())
                .type(r.getType())
                .reason(r.getReason())
                .status(r.getStatus())
                .reportedAt(r.getReportedAt())
                .reporterName(reporterName)
                .targetName(targetName)
                .build();
    }


    private ReportDetailDto toReportDetailDto(Report r) {

        String reporterName = getUserName(r.getReporterId());
        String targetName = getUserName(r.getTargetId());

        return ReportDetailDto.builder()
                .id(r.getId())
                .type(r.getType()) 
                .reporterId(r.getReporterId())
                .reporterName(reporterName)
                .targetId(r.getTargetId())
                .targetName(targetName)
                .reason(r.getReason())
                .status(r.getStatus())
                .reportedAt(r.getReportedAt())
                .resolvedAt(r.getResolvedAt())
                .build();
    }

    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("Unknown User");
    }
}
