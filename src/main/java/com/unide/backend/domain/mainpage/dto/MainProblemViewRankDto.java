package com.unide.backend.domain.mainpage.dto;

import java.time.LocalDate;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MainProblemViewRankDto {

    private final Integer rank; 
    private final Integer delta; 
    
    private final Long problemId;
    private final String title;
    private final ProblemDifficulty difficulty;

    private final Integer views; 
    private final LocalDate rankDate; 
    
    // Repository에서 조회한 기본 데이터를 담는 생성자
    @Builder(builderMethodName = "baseBuilder", toBuilder = true)
    public MainProblemViewRankDto(Long problemId, String title, ProblemDifficulty difficulty, 
                                  Integer views, LocalDate rankDate) {
        this.problemId = problemId;
        this.title = title;
        this.difficulty = difficulty;
        this.views = views;
        this.rankDate = rankDate;
        this.rank = null; 
        this.delta = null; 
    }

    // Service에서 순위 및 델타를 계산하여 최종 DTO를 생성하는 생성자
    public MainProblemViewRankDto(MainProblemViewRankDto baseDto, int rank, int delta) {
        this.rank = rank;
        this.delta = delta;
        this.problemId = baseDto.problemId;
        this.title = baseDto.title;
        this.difficulty = baseDto.difficulty;
        this.views = baseDto.views;
        this.rankDate = baseDto.rankDate;
    }
}