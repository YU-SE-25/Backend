package com.unide.backend.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;

import java.util.Optional;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 상태별 신고 목록 조회
    List<Report> findAllByStatus(ReportStatus status);

    // ID로 신고 조회
    Optional<Report> findById(Long id);

    // 모든 신고 조회
    List<Report> findAll();

    // 신고자 ID로 신고 목록 조회
    List<Report> findAllByReporterId(Long reporterId);
}

