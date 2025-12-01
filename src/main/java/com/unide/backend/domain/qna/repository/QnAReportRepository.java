package com.unide.backend.domain.qna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnAReport;

public interface QnAReportRepository extends JpaRepository<QnAReport, Long>{

}
