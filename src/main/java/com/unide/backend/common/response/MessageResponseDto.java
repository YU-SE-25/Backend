// API 요청 성공 시 간단한 메시지를 반환하는 DTO

package com.unide.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private String message;
}
