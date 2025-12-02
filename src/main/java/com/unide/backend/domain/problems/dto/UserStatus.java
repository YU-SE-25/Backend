package com.unide.backend.domain.problems.dto;

// 회원의 문제 풀이 상태
public enum UserStatus {
    NOT_SOLVED, // 안 풀었음
    CORRECT,    // 맞춤
    INCORRECT   // 틀림
}