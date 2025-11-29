package com.unide.backend.domain.review_report.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.review_report.entity.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

}
