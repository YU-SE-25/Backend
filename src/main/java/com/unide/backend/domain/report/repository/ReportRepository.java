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

<<<<<<< HEAD
    // ID로 신고 조회
    Optional<Report> findById(Long id);

    // 모든 신고 조회
    List<Report> findAll();

    // 신고자 ID로 신고 목록 조회
    List<Report> findAllByReporterId(Long reporterId);
=======
    // 👇 이 두 개는 JpaRepository가 이미 기본 제공하니까 굳이 선언 안 해도 됨
    // Report findReport(Long id);     // ❌ 삭제
    // List<Report> findAll();         // ❌ 삭제 (원래부터 있음)
>>>>>>> 07f83af ([수정]오류 수정)
}
