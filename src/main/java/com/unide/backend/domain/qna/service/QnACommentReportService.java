package com.unide.backend.domain.qna.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.qna.dto.QnACommentReportCreateRequestDto;
import com.unide.backend.domain.qna.entity.QnAComment;
import com.unide.backend.domain.qna.entity.QnACommentReport;
import com.unide.backend.domain.qna.entity.QnACommentReportStatus;
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

    private final QnACommentReportRepository qnACommentReportRepository;
    private final ReportRepository reportRepository;
    private final QnACommentRepository qnACommentRepository;
    private final UserRepository userRepository;

    // QnA 댓글 신고
    public void reportPost(Long commentId, Long reporterId, QnACommentReportCreateRequestDto dto) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAComment comment = qnACommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 1) 공용 reports 저장
        Report baseReport = Report.builder()
                .reporterId(reporterId)
                .targetId(commentId)
                .type(ReportType.PROBLEM)              // 필요시 QNA_COMMENT 등으로 추가
                .status(ReportStatus.PENDING)
                .reason(dto.getReason())
                .reportedAt(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(baseReport);

        // 2) qna_comment_report 저장
        QnACommentReport commentReport = QnACommentReport.builder()
                .reportId(savedReport.getId())             // 🔥 PK = report_id
                .report(savedReport)
                .reporter(reporter)
                .comment(comment)
                .reason(dto.getReason())
                .status(QnACommentReportStatus.UNPROCESS)
                .reportAt(LocalDateTime.now())
                .build();

        qnACommentReportRepository.save(commentReport);
    }
}
