package com.unide.backend.domain.mypage.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUpdateResponseDto {
    private String message;
    private LocalDateTime updatedAt; // 또는 LocalDateTime

    public MyPageUpdateResponseDto(String message, LocalDateTime updatedAt) {
        this.message = message;
        this.updatedAt = updatedAt;
    }
}