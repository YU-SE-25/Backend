// 문제 수정 요청 DTO

package com.unide.backend.domain.problems.dto;

import java.util.List;

import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.ProblemTag;
import com.unide.backend.domain.problems.entity.ProblemStatus;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProblemUpdateRequestDto {
    
    private String title;
    private String description;
    private String inputOutputExample;
    private ProblemDifficulty difficulty;
    
    @Positive(message = "시간 제한은 양수여야 합니다")
    private Integer timeLimit;
    
    @Positive(message = "메모리 제한은 양수여야 합니다")
    private Integer memoryLimit;
    
    private ProblemStatus status;
    
    private List<ProblemTag> tags;
    
    private String hint;
    
    private String source;
}
