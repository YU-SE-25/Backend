// 블랙리스트 확인 결과 응답을 담는 DTO

package com.unide.backend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistCheckResponseDto {
    private boolean isBlacklisted;
    private String message;
}
