// 블랙리스트 등록 응답 DTO

package com.unide.backend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlacklistCreateResponseDto {
    private Long blacklistId;
    private String message;
    private LocalDateTime bannedAt;
}
