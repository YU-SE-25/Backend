package com.unide.backend.domain.qna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnACommentReport;

public interface QnACommentReportRepository extends JpaRepository<QnACommentReport, Long>{

}
