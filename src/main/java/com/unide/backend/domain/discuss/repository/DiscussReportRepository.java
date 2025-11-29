package com.unide.backend.domain.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussReport;

public interface DiscussReportRepository extends JpaRepository<DiscussReport, Long> {
}
