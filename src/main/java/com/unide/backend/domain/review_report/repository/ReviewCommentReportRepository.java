package com.unide.backend.domain.review_report.repository;

import com.unide.backend.domain.review_report.entity.ReviewCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCommentReportRepository extends JpaRepository<ReviewCommentReport, Long> {

}
