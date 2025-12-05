// 강사 지원 정보를 관리하는 엔터티

package com.unide.backend.domain.instructor.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "instructor_application")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstructorApplication extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(length = 500)
    private String portfolioFileUrl;

    @Column(columnDefinition = "TEXT")
    private String portfolioLinks;

    @Column(length = 255)
    private String rejectionReason;

    private LocalDateTime processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processor_id")
    private User processor;

    @Builder
    public InstructorApplication(User user, String portfolioFileUrl, String portfolioLinks) {
        this.user = user;
        this.portfolioFileUrl = portfolioFileUrl;
        this.portfolioLinks = portfolioLinks;
        this.status = ApplicationStatus.PENDING;
    }

    public void approve(User processor) {
        this.status = ApplicationStatus.APPROVED;
        this.processor = processor;
        this.processedAt = LocalDateTime.now();
        this.rejectionReason = null;
    }

    public void reject(User processor, String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.processor = processor;
        this.processedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }
}
