package com.unide.backend.domain.review_report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.entity.ReportType;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.repository.CodeReviewRepository;
import com.unide.backend.domain.review_report.dto.ReviewReportCreateRequestDto;
import com.unide.backend.domain.review_report.entity.ReviewReport;
import com.unide.backend.domain.review_report.repository.ReviewReportRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewReportService {

    private final ReviewReportRepository reviewReportRepository;
    private final ReportRepository reportRepository;
    private final CodeReviewRepository codeReviewRepository;
    private final UserRepository userRepository;
    private final StatsService statsService;

    /**
     * 코드 리뷰 신고
     * 1) reports 테이블에 기본 신고 정보 저장
     * 2) review_report 테이블에 상세 신고 정보 저장
     */
    public void reportPost(Long postId, Long reporterId, ReviewReportCreateRequestDto dto) {

    User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    CodeReview post = codeReviewRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드 리뷰입니다."));

    // 1) 공용 reports 테이블에 저장
    Report baseReport = Report.builder()
            .reporterId(reporterId)
            .targetId(postId)
            .type(ReportType.PROBLEM)   // 네 enum에 맞게
            .status(ReportStatus.PENDING)
            .reason(dto.getReason())
            .reportedAt(LocalDateTime.now())
            .build();

    Report savedReport = reportRepository.save(baseReport);

    // 2) 상세 review_report 저장 (reportId 직접 설정 X)
    ReviewReport reviewReport = new ReviewReport();
    reviewReport.setReport(savedReport);                 // @MapsId 가 여기서 PK 복사
    reviewReport.setReporter(reporter);
    reviewReport.setPost(post);
    reviewReport.setReason(dto.getReason());
    reviewReport.setStatus(savedReport.getStatus());
    reviewReport.setReportAt(LocalDateTime.now());

    reviewReportRepository.save(reviewReport);
}

public void changeStatus(Long reportId, ReportStatus status) {

    Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고 존재하지 않음"));

    report.setStatus(status);

    if (status == ReportStatus.APPROVED) {
        Long reviewId = report.getTargetId(); // 우리가 넣어둔 code_review.id

        CodeReview review = codeReviewRepository.findById(reviewId)  // ✅ 인스턴스로 호출
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        
        Long authorId = review.getReviewer().getId();  // ⭐ 리뷰 작성자
        statsService.onPostReported(authorId);         // -10점
    }
}


}
