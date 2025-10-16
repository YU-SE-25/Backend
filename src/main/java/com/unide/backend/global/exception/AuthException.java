// 인증 관련 비즈니스 예외를 처리하는 클래스

package com.unide.backend.global.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
