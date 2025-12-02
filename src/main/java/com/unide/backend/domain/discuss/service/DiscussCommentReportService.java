package com.unide.backend.domain.discuss.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussCommentReportCreateRequestDto;
import com.unide.backend.domain.discuss.entity.DiscussComment;
import com.unide.backend.domain.discuss.entity.DiscussCommentReport;
import com.unide.backend.domain.discuss.repository.DiscussCommentReportRepository;
import com.unide.backend.domain.discuss.repository.DiscussCommentRepository;
import com.unide.backend.domain.mypage.service.StatsService;
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
    private final StatsService statsService;

    /**
     * 댓글 신고: reports + dis_comment_report 저장
     */
    public void reportPost(Long commentId, Long reporterId, DiscussCommentReportCreateRequestDto dto) {

        LocalDateTime now = LocalDateTime.now();

        // 1) 신고자 검증
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2) 신고 대상 댓글 검증
        DiscussComment comment = discussCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 3) reports 테이블 저장 (공통 테이블)
        Report baseReport = Report.builder()
                .reporterId(reporterId)
                .targetId(commentId)              // 어떤 댓글이 신고 대상인지
                .type(ReportType.PROBLEM)         // 필요하면 DISCUSS_COMMENT 로 새로 추가해도 됨
                .status(ReportStatus.PENDING)
                .reason(dto.getReason())
                .reportedAt(now)
                .build();

        Report savedReport = reportRepository.save(baseReport);

        // 4) dis_comment_report 저장
        DiscussCommentReport commentReport = new DiscussCommentReport();
        // PK 가 report_id (@MapsId) 라면 이거 세팅
        commentReport.setReportId(savedReport.getId());
        commentReport.setReport(savedReport);
        commentReport.setReporter(reporter);
        commentReport.setComment(comment);
        commentReport.setReason(dto.getReason());
        commentReport.setStatus(savedReport.getStatus());
   // 🔥 여기서도 같은 enum 사용
        commentReport.setReportAt(now);

        discussCommentReportRepository.save(commentReport);
    }

    /**
     * 신고 상태 변경 (관리자용)
     */
    public void changeStatus(Long reportId, ReportStatus status) {

        // 1) 공통 report 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 존재하지 않음"));

        // 2) 공통 테이블 상태 변경
        report.setStatus(status);

        // 3) 세부 테이블(dis_comment_report)도 상태 동기화
        discussCommentReportRepository.findById(reportId)
                .ifPresent(r -> r.setStatus(status));

        // 4) 승인(APPROVED)이면 댓글 작성자 평판 차감
        if (status == ReportStatus.APPROVED) {
            Long commentId = report.getTargetId();   // targetId = 댓글 ID

            DiscussComment comment = discussCommentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("댓글 존재하지 않음"));

            Long authorId = comment.getAuthorId();   // 댓글 작성자 ID

            statsService.onPostReported(authorId);   // -10점
        }
    }
}
