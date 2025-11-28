package com.unide.backend.domain.report.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResolveRequestDto {
    private String adminAction;  // ex: USER_WARNING, CONTENT_DELETE
    private String adminReason;
}

