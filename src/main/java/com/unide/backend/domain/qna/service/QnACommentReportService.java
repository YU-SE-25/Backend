package com.unide.backend.domain.qna.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.qna.dto.QnACommentReportCreateRequestDto;
import com.unide.backend.domain.qna.entity.QnAComment;
import com.unide.backend.domain.qna.entity.QnACommentReport;
import com.unide.backend.domain.qna.repository.QnACommentReportRepository;
import com.unide.backend.domain.qna.repository.QnACommentRepository;
import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.entity.ReportType;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QnACommentReportService {

    private final UserRepository userRepository;
    private final QnACommentRepository qnACommentRepository;
    private final QnACommentReportRepository qnACommentReportRepository;
    private final ReportRepository reportRepository;

    /**
     * QnA 댓글 신고
     */
    public void reportPost(Long commentId, Long reporterId, QnACommentReportCreateRequestDto dto) {

        LocalDateTime now = LocalDateTime.now();

        // ===== 1) 유효성 검사 =====
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAComment comment = qnACommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // ===== 2) 공통 Report 저장 =====
        Report baseReport = Report.builder()
                .reporterId(reporterId)
                .targetId(commentId)
                .type(ReportType.PROBLEM) // 댓글 신고
                .status(ReportStatus.PENDING)
                .reason(dto.getReason())
                .reportedAt(now)
                .build();

        Report savedReport = reportRepository.save(baseReport);

        // ===== 3) QnA 댓글 전용 report 저장 =====
        QnACommentReport commentReport = new QnACommentReport();
        commentReport.setReportId(savedReport.getId()); // FK
        commentReport.setReport(savedReport);           // 연관관계
        commentReport.setReporter(reporter);
        commentReport.setComment(comment);
        commentReport.setReason(dto.getReason());
        commentReport.setStatus(savedReport.getStatus());
        commentReport.setReportAt(now);

        qnACommentReportRepository.save(commentReport);
    }
}
