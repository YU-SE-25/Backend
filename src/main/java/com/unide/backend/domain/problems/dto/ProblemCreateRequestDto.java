// 문제 등록 요청 DTO

package com.unide.backend.domain.problems.dto;

import java.util.List;

import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.ProblemStatus;
import com.unide.backend.domain.problems.entity.ProblemTag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ProblemCreateRequestDto {
    
    @NotBlank(message = "문제 제목은 필수입니다")
    private String title;

    @NotBlank(message = "문제 요약은 필수입니다.")
    @Size(max = 255, message = "요약은 255자를 초과할 수 없습니다.")
    private String summary;
    
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
    private List<TestCaseDto> testCases;
}
