package com.unide.backend.domain.mainpage.entity;

import java.time.LocalDate;
import com.unide.backend.domain.problems.entity.Problems; // Problems 엔터티는 별도 구현되어야 함
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "problem_view_rank") // 테이블 이름은 그대로 유지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MainProblemViewRankEntity { // 클래스 이름 수정

    // 문제 조회수 주간 순위 고유 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 문제 ID (FK Problems.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problems problem;

    // 문제 조회수 
    @Column(nullable = false)
    private Integer views;

    // 문제 조회수가 업데이트된 시간
    @Column(nullable = false)
    private LocalDate date; 

    @Builder
    public MainProblemViewRankEntity(Problems problem, Integer views, LocalDate date) {
        this.problem = problem;
        this.views = views;
        this.date = date;
    }
}