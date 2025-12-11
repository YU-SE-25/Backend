package com.unide.backend.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorApproveEmailRequestDto {
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}