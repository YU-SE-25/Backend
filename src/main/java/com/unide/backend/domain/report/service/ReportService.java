package com.unide.backend.domain.report.service;

import com.unide.backend.domain.report.dto.ReportCreateRequestDto;
import com.unide.backend.domain.report.dto.ReportDetailDto;
import com.unide.backend.domain.report.dto.ReportListDto;
import com.unide.backend.domain.report.entity.Report;
import com.unide.backend.domain.report.entity.ReportStatus;
import com.unide.backend.domain.report.repository.ReportRepository;
import com.unide.backend.domain.report.entity.ReportType;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProblemsRepository problemsRepository;
    private final com.unide.backend.common.mail.MailService mailService;
    
    /** 신고 상태 변경 및 이메일 전송 */
    @Transactional
    public void updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 정보를 찾을 수 없습니다."));
        report.setStatus(status);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);

        // 이메일 전송
        User reporter = userRepository.findById(report.getReporterId()).orElse(null);
        if (reporter != null && reporter.getEmail() != null) {
            String subject = "[UnIDE] 신고 처리 결과 안내";
            String body;
            if (status == ReportStatus.REJECTED) {
                body = String.format("안녕하세요, %s님.\n\n신고하신 내용이 거절되었습니다.\n\n감사합니다.", reporter.getNickname());
            } else {
                body = String.format("안녕하세요, %s님.\n\n신고 상태가 승인되었습니다.\n\n감사합니다.", reporter.getNickname());
            }
            mailService.sendEmail(reporter.getEmail(), subject, body);
        }
    }

    /**
     * 신고 생성
     */
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


    /**
     * 내가 한 신고 목록 조회
     */
    public List<ReportListDto> getMyReports(Long userId) {

        return reportRepository.findAllByReporterId(userId)
                .stream()
                .map(this::toListDto)
                .toList();
    }


    /**
     * 신고 상세 조회 (내 신고만 가능)
     */
    public ReportDetailDto getMyReportDetail(Long userId, Long reportId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고 정보를 찾을 수 없습니다."));

        if (!report.getReporterId().equals(userId)) {
            throw new IllegalAccessError("본인이 한 신고만 조회할 수 있습니다.");
        }

        return toDetailDto(report);
    }

    // ==============================================================
    // 내부 DTO 변환 메서드 (Builder 방식) — ★ 여기서 에러 모두 해결됨
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
    // 내부 공통 유틸 메서드
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
