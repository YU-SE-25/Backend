package com.unide.backend.domain.report.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long targetId;    // 유저ID 또는 문제ID b

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;  // 신고 유형(USER, PROBLEM)

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String title;

    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
}
