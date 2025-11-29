package com.unide.backend.domain.review_report.dto;

public class ReviewReportCreateRequestDto {

    private String reason;

    public ReviewReportCreateRequestDto() {
    }

    public ReviewReportCreateRequestDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
