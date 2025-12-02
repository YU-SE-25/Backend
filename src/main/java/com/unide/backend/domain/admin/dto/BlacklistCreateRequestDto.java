// 블랙리스트 등록 요청 DTO

package com.unide.backend.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlacklistCreateRequestDto {
    private String email;
    private String phone;
    private String name;

    @NotBlank(message = "등록 사유는 필수입니다.")
    private String reason;
}
