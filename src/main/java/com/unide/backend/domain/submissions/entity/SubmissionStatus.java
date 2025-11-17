// 코드 채점 상태를 나타내는 Enum

package com.unide.backend.domain.submissions.entity;

public enum SubmissionStatus {
    PENDING,  // 채점 대기 중
    GRADING,  // 채점 중
    CA,       // 정답 (Correct Answer)
    WA,       // 오답 (Wrong Answer)
    CE,       // 컴파일 에러 (Compile Error)
    RE,       // 런타임 에러 (Runtime Error)
    TLE,      // 시간 초과 (Time Limit Exceeded)
    MLE,      // 메모리 초과 (Memory Limit Exceeded)
    DRAFT     // 임시 저장
}
