package com.unide.backend.domain.report.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.common.mail.MailService;
import com.unide.backend.domain.discuss.repository.DiscussCommentReportRepository;
import com.unide.backend.domain.discuss.repository.DiscussReportRepository;
import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.qna.entity.QnAReport;
import com.unide.backend.domain.qna.repository.QnACommentReportRepository;
import com.unide.backend.domain.qna.repository.QnAReportRepository;
import com.unide.backend.domain.report.dto.ReportCreateRequestDto;
import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.entity.ReportType;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.review_report.repository.ReviewCommentReportRepository;
import com.unide.backend.domain.review_report.repository.ReviewReportRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProblemsRepository problemsRepository;
    private final MailService mailService;
    private final StatsService statsService;

    private final DiscussReportRepository discussReportRepository;
    private final DiscussCommentReportRepository discussCommentReportRepository;
    private final QnAReportRepository qnaReportRepository;
    private final QnACommentReportRepository qnaCommentReportRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewCommentReportRepository reviewCommentReportRepository;


    // ==============================================================
    // 1. 신고 상태 변경 + 이메일 + 평판반영
    // ==============================================================

    @Transactional
    public void updateReportStatus(Long reportId, ReportStatus status) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 정보를 찾을 수 없습니다."));

        report.setStatus(status);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);

        // 승인일 때만 평판 감소
        if (status == ReportStatus.APPROVED) {
            applyPenalty(report);
        }

        // 이메일 전송
        User reporter = userRepository.findById(report.getReporterId()).orElse(null);
        if (reporter != null && reporter.getEmail() != null) {
            String subject = "[UnIDE] 신고 처리 결과 안내";
            String body;

            if (status == ReportStatus.REJECTED) {
                body = String.format(
                        "안녕하세요, %s님.\n\n신고하신 내용이 거절되었습니다.\n\n감사합니다.",
                        reporter.getNickname()
                );
            } else {
                body = String.format(
                        "안녕하세요, %s님.\n\n신고 상태가 승인되었습니다.\n\n감사합니다.",
                        reporter.getNickname()
                );
            }

            mailService.sendEmail(reporter.getEmail(), subject, body);
        }
    }


    // ==============================================================
    // 2. 승인된 신고 → 작성자 평판 감소
    // ==============================================================

    private void applyPenalty(Report report) {

        Long authorId = null;
        Long reportId = report.getId();

        // 1) 유저 신고
        if (report.getType() == ReportType.USER) {
            authorId = report.getTargetId();
        }

        // 2) 게시글/댓글 신고 (dis/qna/review 모두 PROBLEM)
        else if (report.getType() == ReportType.PROBLEM) {

            // (1) 디스커스 게시글 신고
            var disPostOpt = discussReportRepository.findById(reportId);
            if (authorId == null && disPostOpt.isPresent()) {
                authorId = disPostOpt.get().getPost().getAuthorId();
            }

            // (2) 디스커스 댓글 신고
            var disCommentOpt = discussCommentReportRepository.findById(reportId);
            if (authorId == null && disCommentOpt.isPresent()) {
                authorId = disCommentOpt.get().getComment().getAuthorId();
            }

            // (3) QnA 게시글 신고
            QnAReport qnaReport = qnaReportRepository.findById(reportId).orElse(null);
            if (authorId == null && qnaReport != null) {
                if (qnaReport.getPost() != null && qnaReport.getPost().getAuthor() != null) {
                    authorId = qnaReport.getPost().getAuthor().getId();
                }
            }

            // (4) QnA 댓글 신고
            var qnaCommentOpt = qnaCommentReportRepository.findById(reportId);
            if (authorId == null && qnaCommentOpt.isPresent()) {
                authorId = qnaCommentOpt.get().getComment().getAuthorId();
            }

            // (5) 리뷰 게시글 신고
            var reviewPostOpt = reviewReportRepository.findById(reportId);
            if (authorId == null && reviewPostOpt.isPresent()) {
                var reviewPost = reviewPostOpt.get().getPost();
                if (reviewPost != null && reviewPost.getReviewer() != null) {
                    authorId = reviewPost.getReviewer().getId();
                }
            }

            // (6) 리뷰 댓글 신고
            var reviewCommentOpt = reviewCommentReportRepository.findById(reportId);
            if (authorId == null && reviewCommentOpt.isPresent()) {
                var reviewComment = reviewCommentOpt.get().getReviewComment();
                if (reviewComment != null && reviewComment.getCommenter() != null) {
                    authorId = reviewComment.getCommenter().getId();
                }
            }
        }

        // 최종 평판 감소
        if (authorId != null) {
            statsService.onPostReported(authorId);
        }
    }


    // ==============================================================
    // 3. 신고 생성
    // ==============================================================

    @Transactional
    public void createReport(Long reporterId, ReportCreateRequestDto dto) {

        Report report = Report.builder()
                .reporterId(reporterId)
                .targetId(dto.getTargetId())
                .type(dto.getType())
                .reason(dto.getReason())
                .status(ReportStatus.PENDING)
                .reportedAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);
    }


    // ==============================================================
    // 4. 신고 목록 / 상세
    // ==============================================================

    public List<ReportListDto> getMyReports(Long userId) {
        return reportRepository.findAllByReporterId(userId)
                .stream()
                .map(this::toListDto)
                .toList();
    }

    public ReportDetailDto getMyReportDetail(Long userId, Long reportId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 정보를 찾을 수 없습니다."));

        if (!report.getReporterId().equals(userId)) {
            throw new IllegalAccessError("본인이 한 신고만 조회할 수 있습니다.");
        }

        return toDetailDto(report);
    }


    // ==============================================================
    // 5. DTO 변환
    // ==============================================================

    private ReportListDto toListDto(Report r) {

        String reporterName = getUserName(r.getReporterId());
        String targetName = getTargetName(r.getType(), r.getTargetId());

        return ReportListDto.builder()
                .id(r.getId())
                .reporterName(reporterName)
                .targetName(targetName)
                .reason(r.getReason())
                .type(r.getType())
                .status(r.getStatus())
                .reportedAt(r.getReportedAt())
                .build();
    }

    private ReportDetailDto toDetailDto(Report r) {

        String reporterName = getUserName(r.getReporterId());
        String targetName = getTargetName(r.getType(), r.getTargetId());

        return ReportDetailDto.builder()
                .id(r.getId())
                .reporterId(r.getReporterId())
                .reporterName(reporterName)
                .targetId(r.getTargetId())
                .targetName(targetName)
                .type(r.getType())
                .reason(r.getReason())
                .status(r.getStatus())
                .reportedAt(r.getReportedAt())
                .resolvedAt(r.getResolvedAt())
                .build();
    }


    // ==============================================================
    // 6. 공통 유틸
    // ==============================================================

    private String getUserName(Long id) {
        return userRepository.findById(id)
                .map(User::getNickname)
                .orElse("Unknown User");
    }

    private String getTargetName(ReportType type, Long id) {

        if (type == ReportType.USER) {
            return userRepository.findById(id)
                    .map(User::getNickname)
                    .orElse("Unknown User");
        }

        return problemsRepository.findById(id)
                .map(Problems::getTitle)
                .orElse("Unknown Problem");
    }
}
