// 특정 강사 지원 건의 상세 정보를 담는 DTO

package com.unide.backend.domain.admin.dto;

import com.unide.backend.domain.instructor.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorApplicationDetailDto {
    private Long applicationId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime submittedAt;
    private ApplicationStatus status;
    private String portfolioFileUrl;
    private String portfolioLinks;
    private String rejectionReason;
    private LocalDateTime processedAt;
    private Long processorId;
    private String processorName;
}
