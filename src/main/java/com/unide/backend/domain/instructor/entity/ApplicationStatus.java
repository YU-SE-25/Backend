// 강사 지원서의 처리 상태를 나타내는 

package com.unide.backend.domain.instructor.entity;

public enum ApplicationStatus {
    PENDING,  // 승인 대기
    APPROVED, // 승인됨
    REJECTED  // 거절됨
}
