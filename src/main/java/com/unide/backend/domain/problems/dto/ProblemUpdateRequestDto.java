// 문제 수정 요청 DTO

package com.unide.backend.domain.problems.dto;

import java.util.List;

import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.ProblemTag;
import com.unide.backend.domain.problems.entity.ProblemStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor
public class ProblemUpdateRequestDto {
    @NotBlank(message = "문제 제목은 필수입니다")
    private String title;

    @NotBlank(message = "문제 설명은 필수입니다")
    private String description;

    @NotBlank(message = "입출력 예시는 필수입니다")
    private String inputOutputExample;

    @NotNull(message = "난이도는 필수입니다")
    private ProblemDifficulty difficulty;
    
    @NotNull(message = "시간 제한은 필수입니다")
    @Positive(message = "시간 제한은 양수여야 합니다")
    private Integer timeLimit;
    
    @NotNull(message = "메모리 제한은 필수입니다")
    @Positive(message = "메모리 제한은 양수여야 합니다")
    private Integer memoryLimit;
    
    private ProblemStatus status;
    
    private List<ProblemTag> tags;
    
    private String hint;
    
    private String source;

    @NotNull(message = "테스트케이스 파일은 필수입니다")
    private MultipartFile testcaseFile;
}
