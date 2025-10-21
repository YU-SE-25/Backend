// 관리자가 강사 지원 상태를 변경할 때 요청을 담는 DTO

package com.unide.backend.domain.admin.dto;

import com.unide.backend.domain.instructor.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstructorApplicationUpdateRequestDto {

    @NotNull(message = "처리 상태는 필수 입력 값입니다 (APPROVED 또는 REJECTED).")
    private ApplicationStatus status; // APPROVED 또는 REJECTED

    private String rejectionReason; // 거절 시에만 사용
}
