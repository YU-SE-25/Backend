// problems 테이블과 매핑되는 엔터티

package com.unide.backend.domain.problems.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "problems")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problems extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Lob
    @Column(nullable = false)
    private String inputOutputExample;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemDifficulty difficulty;

    @Column(nullable = false)
    private Integer timeLimit;

    @Column(nullable = false)
    private Integer memoryLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemVisibility visibility;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Builder
    public Problems(String title, String description, String inputOutputExample, ProblemDifficulty difficulty,
                    Integer timeLimit, Integer memoryLimit, ProblemVisibility visibility) {
        this.title = title;
        this.description = description;
        this.inputOutputExample = inputOutputExample;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.visibility = (visibility != null) ? visibility : ProblemVisibility.PUBLIC;
        this.viewCount = 0;
    }
}
