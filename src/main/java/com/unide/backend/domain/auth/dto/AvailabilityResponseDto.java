// 이메일/닉네임 등의 사용 가능 여부를 반환하는 응답 DTO

package com.unide.backend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDto {
    private boolean isAvailable;
    private String message;
}
