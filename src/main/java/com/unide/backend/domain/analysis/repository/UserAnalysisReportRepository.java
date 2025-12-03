package com.unide.backend.domain.analysis.repository;

import com.unide.backend.domain.analysis.entity.UserAnalysisReport;
import com.unide.backend.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAnalysisReportRepository extends JpaRepository<UserAnalysisReport, Long> {
    Optional<UserAnalysisReport> findTopByUserOrderByCreatedAtDesc(User user);
}
