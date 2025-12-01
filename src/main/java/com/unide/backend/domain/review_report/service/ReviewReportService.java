package com.unide.backend.domain.review_report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.entity.ReportType;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.repository.CodeReviewRepository;
import com.unide.backend.domain.review_report.dto.ReviewReportCreateRequestDto;
import com.unide.backend.domain.review_report.entity.ReviewReport;
import com.unide.backend.domain.review_report.entity.ReviewReportStatus;
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
    reviewReport.setStatus(ReviewReportStatus.UNPROCESS);
    reviewReport.setReportAt(LocalDateTime.now());

    reviewReportRepository.save(reviewReport);
}

}
