package com.unide.backend.domain.qna.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.domain.qna.dto.QnAReportCreateRequestDto;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnAReport;
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
    private final StatsService statsService;
// QnA 게시글 신고
public void reportPost(Long postId, Long reporterId, QnAReportCreateRequestDto dto) {
    User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    QnA post = qnARepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

    Report baseReport = Report.builder()
            .reporterId(reporterId)
            .targetId(postId)
            .type(ReportType.QnA)
            .status(ReportStatus.PENDING)
            .reason(dto.getReason())
            .reportedAt(LocalDateTime.now())
            .build();

    Report savedReport = reportRepository.save(baseReport);

    QnAReport qnAReport = QnAReport.builder()
            .reportId(savedReport.getId())
            .report(savedReport)
            .reporter(reporter)
            .post(post)
            .reason(dto.getReason())
            .status(savedReport.getStatus())
            .reportAt(LocalDateTime.now())
            .build();

    qnAReportRepository.save(qnAReport);
}

/**
 * 신고 상태 변경 (관리자용)
 * APPROVED 로 확정될 때만 게시글 작성자의 평판 점수 -10
 */
public void changeStatus(Long reportId, ReportStatus status) {

    Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고 존재하지 않음"));

    report.setStatus(status);

    if (status == ReportStatus.APPROVED) {
        Long postId = report.getTargetId();
        QnA post = qnARepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 존재하지 않음"));

        // QnA 엔티티에 맞게 아래 둘 중 하나로 선택
        // Long authorId = post.getAuthorId();
        Long authorId = post.getAuthor().getId();

        statsService.onPostReported(authorId);
    }
}


}
