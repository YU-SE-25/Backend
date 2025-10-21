// 강사 지원 목록의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.admin.dto;

import com.unide.backend.domain.instructor.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorApplicationSummaryDto {
    private Long applicationId;
    private String name; // 지원자 이름
    private String email; // 지원자 이메일
    private LocalDateTime submittedAt; // 제출 시각
    private ApplicationStatus status; // 현재 상태 (PENDING)
}
