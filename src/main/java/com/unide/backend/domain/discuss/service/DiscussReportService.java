package com.unide.backend.domain.discuss.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussReportCreateRequestDto;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.entity.DiscussReport;
import com.unide.backend.domain.discuss.repository.DiscussReportRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
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
public class DiscussReportService {

    private final DiscussReportRepository discussReportRepository;
    private final ReportRepository reportRepository;
    private final DiscussRepository discussRepository;
    private final UserRepository userRepository;
    private final StatsService statsService;

    // 🔹 게시글 신고 (Report → discuss_report 순서)
    public void reportPost(Long postId, Long reporterId, DiscussReportCreateRequestDto dto) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 1) 공용 reports 테이블에 한 건 생성
        Report baseReport = Report.builder()
                .reporterId(reporterId)
                .targetId(postId)
                .type(ReportType.DISCUSSION)              // 필요하면 DISCUSS_POST 등으로 enum 추가
                .status(ReportStatus.PENDING)
                .reason(dto.getReason())
                .reportedAt(LocalDateTime.now())
                .build();

        Report savedReport = reportRepository.save(baseReport);   // 여기서 report_id 생성

        // 2) discuss_report 에 저장 (FK + PK = savedReport.getId())
        DiscussReport discussReport = DiscussReport.builder()
                .reportId(savedReport.getId())                   // 🔴 PK 직접 세팅 (여기가 핵심)
                .report(savedReport)                             // 연관관계
                .reporter(reporter)
                .post(post)
                .reason(dto.getReason())
                .status(savedReport.getStatus()) 
                .reportAt(LocalDateTime.now())
                .build();

        discussReportRepository.save(discussReport);
    }
    
    /**
     * 신고 상태 변경 (관리자용)
     * APPROVED 로 확정될 때만 댓글 작성자의 평판 점수 -10
     */
    public void changeStatus(Long reportId, ReportStatus status) {

        // 1) report 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 존재하지 않음"));

        // 2) 상태 변경
        report.setStatus(status);

        // 3) 승인된 경우에만 평판 점수 차감
        if (status == ReportStatus.APPROVED) {
            Long postId = report.getTargetId(); // 우리가 게시글 id
            Discuss post = discussRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글 존재하지 않음"));

            Long authorId = post.getAuthorId();   // 게시글 작성자
            statsService.onPostReported(authorId);   // -10점
        }
    }
}
