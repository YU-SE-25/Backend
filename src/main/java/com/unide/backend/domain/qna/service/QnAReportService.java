package com.unide.backend.domain.qna.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.qna.dto.QnAReportCreateRequestDto;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnAReport;
import com.unide.backend.domain.qna.entity.QnAReportStatus;
import com.unide.backend.domain.qna.repository.QnAReportRepository;
import com.unide.backend.domain.qna.repository.QnARepository;
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
public class QnAReportService {

    private final QnAReportRepository qnAReportRepository;
    private final ReportRepository reportRepository;
    private final QnARepository qnARepository;
    private final UserRepository userRepository;

    // QnA 게시글 신고
    public void reportPost(Long postId, Long reporterId, QnAReportCreateRequestDto dto) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        QnA post = qnARepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 1) 공용 reports 에 먼저 저장
        Report baseReport = Report.builder()
                .reporterId(reporterId)
                .targetId(postId)
                .type(ReportType.PROBLEM)          // 필요시 QNA_POST 등으로 enum 확장 가능
                .status(ReportStatus.PENDING)
                .reason(dto.getReason())
                .reportedAt(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(baseReport);

        // 2) qna_report 에 저장 (PK = report_id)
        QnAReport qnAReport = QnAReport.builder()
                .reportId(savedReport.getId())          // 🔥 PK 수동 세팅
                .report(savedReport)
                .reporter(reporter)
                .post(post)
                .reason(dto.getReason())
                .status(QnAReportStatus.UNPROCESS)
                .reportAt(LocalDateTime.now())
                .build();

        qnAReportRepository.save(qnAReport);
    }
}
