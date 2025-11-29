package com.unide.backend.domain.discuss.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussCommentReportCreateRequestDto;
import com.unide.backend.domain.discuss.entity.DiscussComment;
import com.unide.backend.domain.discuss.entity.DiscussCommentReport;
import com.unide.backend.domain.discuss.entity.DiscussCommentReportStatus;
import com.unide.backend.domain.discuss.repository.DiscussCommentReportRepository;
import com.unide.backend.domain.discuss.repository.DiscussCommentRepository;
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
public class DiscussCommentReportService {

    private final DiscussCommentReportRepository discussCommentReportRepository;
    private final ReportRepository reportRepository;
    private final DiscussCommentRepository discussCommentRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 신고: (1) reports 저장 → (2) dis_comment_report 저장
     */
    public void reportPost(Long commentId, Long reporterId, DiscussCommentReportCreateRequestDto dto) {

        // 신고자 검증
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 신고 대상 댓글 검증
        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 1) 공용 reports 테이블에 먼저 저장
        Report baseReport = Report.builder()
                .reporterId(reporterId)
                .targetId(commentId)                   // 🔹 target = 댓글 ID
                .type(ReportType.PROBLEM)              // 필요시 DISCUSS_COMMENT enum 추가 가능
                .status(ReportStatus.PENDING)
                .reason(dto.getReason())
                .reportedAt(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(baseReport);    // 🔹 report_id 생성됨

        // 2) dis_comment_report 에 저장 (PK = report_id)
        DiscussCommentReport commentReport = DiscussCommentReport.builder()
                .reportId(savedReport.getId())         // 🔥 PK 수동 할당
                .report(savedReport)                   // 연관관계
                .reporter(reporter)
                .comment(comment)
                .reason(dto.getReason())
                .status(DiscussCommentReportStatus.UNPROCESS)
                .reportAt(LocalDateTime.now())
                .build();

        discussCommentReportRepository.save(commentReport);
    }
}
