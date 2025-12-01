package com.unide.backend.domain.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // 상태별 신고 목록 조회
    List<Report> findAllByStatus(ReportStatus status);

    // 🔹 유저가 신고한 목록 조회 (ReportService에서 사용 중)
    List<Report> findAllByReporterId(Long reporterId);

    // 필요하면 이런 식으로 단건 조회용 메서드도 쓸 수 있음
    // Report findReportById(Long id);
}