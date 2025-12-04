// 문제별 테스트 케이스(입력/정답)를 관리하는 엔터티

package com.unide.backend.domain.problems.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "test_cases")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problems problem;

    @Lob
    @Column(nullable = false)
    private String input;

    @Lob
    @Column(nullable = false)
    private String output;

    @Builder
    public TestCase(Problems problem, String input, String output) {
        this.problem = problem;
        this.input = input;
        this.output = output;
    }

    public void setProblem(Problems problem) {
        this.problem = problem;
    }
}
