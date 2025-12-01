package com.unide.backend.domain.review_report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.entity.ReportType;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.entity.CodeReviewComment;
import com.unide.backend.domain.review.repository.CodeReviewCommentRepository;
import com.unide.backend.domain.review_report.dto.ReviewCommentReportCreateRequestDto;
import com.unide.backend.domain.review_report.entity.ReviewCommentReport;
import com.unide.backend.domain.review_report.entity.ReviewCommentReportStatus;
import com.unide.backend.domain.review_report.repository.ReviewCommentReportRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommentService {

    private final ReviewCommentReportRepository reviewCommentReportRepository;
    private final ReportRepository reportRepository;
    private final CodeReviewCommentRepository codeReviewCommentRepository;
    private final UserRepository userRepository;

    /**
     * 코드 리뷰 댓글 신고
     * 1) reports 테이블에 기본 신고 정보 저장
     * 2) review_comment_report 테이블에 상세 신고 정보 저장
     */
   public void reportComment(Long postId,
                          Long commentId,
                          Long reporterId,
                          ReviewCommentReportCreateRequestDto dto) {
    User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    CodeReviewComment comment = codeReviewCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

    // 댓글이 달린 원본 리뷰
    CodeReview post = comment.getReview();   // comment -> review 연관관계 사용

    // (선택) URL의 postId와 실제 댓글의 reviewId가 같은지 검증
    if (!post.getId().equals(postId)) {
        throw new IllegalArgumentException("게시글과 댓글 정보가 일치하지 않습니다.");
    }

    // 1) reports 저장
    Report baseReport = Report.builder()
            .reporterId(reporterId)
            .targetId(commentId)
            .type(ReportType.PROBLEM)   // 네 enum에 맞게
            .status(ReportStatus.PENDING)
            .reason(dto.getReason())
            .reportedAt(LocalDateTime.now())
            .build();

    Report savedReport = reportRepository.save(baseReport);

    // 2) code_review_comment_report 저장
    ReviewCommentReport commentReport = new ReviewCommentReport();
    commentReport.setReport(savedReport);                   // @MapsId → reportId 채워짐
    commentReport.setReporter(reporter);
    commentReport.setReviewComment(comment);
    commentReport.setPost(post);                            // 🔹 post_id 채움
    commentReport.setReason(dto.getReason());
    commentReport.setStatus(ReviewCommentReportStatus.UNPROCESS);
    commentReport.setReportAt(LocalDateTime.now());

    reviewCommentReportRepository.save(commentReport);
}

}
