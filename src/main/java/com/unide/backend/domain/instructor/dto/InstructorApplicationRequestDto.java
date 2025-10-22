// 강사 지원서 최종 제출 시 클라이언트의 요청을 담는 DTO

package com.unide.backend.domain.instructor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InstructorApplicationRequestDto {
    @NotBlank(message = "업로드된 파일 식별자(URL 또는 키)는 필수입니다.")
    private String uploadedFileUrl;

    private List<String> portfolioLinks;
}
