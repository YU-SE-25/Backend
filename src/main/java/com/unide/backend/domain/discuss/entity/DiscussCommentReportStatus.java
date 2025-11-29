package com.unide.backend.domain.discuss.entity;

public enum DiscussCommentReportStatus {
    PROCESS,    // 처리 완료
    UNPROCESS,  // 아직 처리 안 됨
    PENDING     // 검토 대기 (원하면 안 써도 됨)
}
