// 강사 지원서 제출 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.instructor.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstructorApplicationResponseDto {
    private Long applicationId;
    private String message;
}
