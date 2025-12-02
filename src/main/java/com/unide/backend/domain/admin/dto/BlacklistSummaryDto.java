// 블랙리스트 목록의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlacklistSummaryDto {
    private Long blacklistId;
    private String name;
    private String email;
    private String phone;
    private String reason;
    private LocalDateTime bannedAt;
}
