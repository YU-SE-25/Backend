package com.unide.backend.domain.report.dto;

import com.unide.backend.domain.report.entity.ReportStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResolveRequestDto {

    @NotNull(message = "처리 상태는 필수입니다. (APPROVED 또는 REJECTED)")
    private ReportStatus status;   // 🔥 여기 추가

    private String adminAction;    // ex) USER_WARNING, CONTENT_DELETE
    private String adminReason;    // 관리자 메모
}
