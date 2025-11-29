package com.unide.backend.domain.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussCommentReport;

public interface DiscussCommentReportRepository extends JpaRepository<DiscussCommentReport, Long> {
}