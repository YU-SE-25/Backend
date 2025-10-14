// 사용자 역할을 정의하는 열거형 클래스

package com.unide.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    LEARNER("ROLE_LEARNER"),
    INSTRUCTOR("ROLE_INSTRUCTOR"),
    MANAGER("ROLE_MANAGER");

    private final String key;
}
