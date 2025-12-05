package com.unide.backend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlacklistDeleteResponseDto {
    private Long blacklistId;
    private String message;
    private LocalDateTime unbannedAt;
}