// 사용자 상태를 정의하는 열거형 클래스

package main.java.com.unide.backend.domain.user.entity;

public enum UserStatus {
    PENDING,    // 이메일 인증 전 대기 상태
    ACTIVE,     // 활성 상태
    SUSPENDED,  // 정지 상태
    DELETED     // 탈퇴 상태
}
