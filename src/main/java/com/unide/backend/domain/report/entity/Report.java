package com.unide.backend.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reporterId;  // 신고자

    @Column(nullable = false)
    private Long targetId;    // 유저ID 또는 문제ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;  // 신고 유형(USER, PROBLEM)

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(nullable = false)
    private String reason;

    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
}
