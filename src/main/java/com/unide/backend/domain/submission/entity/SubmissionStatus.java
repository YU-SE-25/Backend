package com.unide.backend.domain.submission.entity;

public enum SubmissionStatus {
    PENDING("대기중"),
    GRADING("채점중"),
    AC("정답"),
    WA("오답"),
    CE("컴파일 에러"),
    RE("런타임 에러"),
    TLE("시간 초과"),
    MLE("메모리 초과");

    private final String description;

    SubmissionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
