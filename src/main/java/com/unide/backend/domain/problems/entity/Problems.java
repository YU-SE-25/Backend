// problems 테이블과 매핑되는 엔터티

package com.unide.backend.domain.problems.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "problems")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problems extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

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
    private ProblemStatus status;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ProblemTag> tags = new ArrayList<>();

    @Lob
    private String hint;

    @Column(length = 100)
    private String source;

    @Column(name = "testcase_file_path", length = 255)
    private String testcaseFilePath;

    @Builder
    public Problems(User createdBy, String title, String description, String inputOutputExample, ProblemDifficulty difficulty,
                    Integer timeLimit, Integer memoryLimit, ProblemStatus status, List<ProblemTag> tags,
                    String hint, String source, String testcaseFilePath) {
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.inputOutputExample = inputOutputExample;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.status = (status != null) ? status : ProblemStatus.PENDING;
        this.viewCount = 0;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        this.hint = hint;
        this.source = source;
        this.testcaseFilePath = testcaseFilePath;
    }
    
    // 비즈니스 메서드
    public void updateTitle(String title) {
        this.title = title;
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
    
    public void updateInputOutputExample(String inputOutputExample) {
        this.inputOutputExample = inputOutputExample;
    }
    
    public void updateDifficulty(ProblemDifficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public void updateTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public void updateMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
    
    public void updateStatus(ProblemStatus status) {
        this.status = status;
    }
    
    public void increaseViewCount() {
        this.viewCount++;
    }
    
    public void updateTags(List<ProblemTag> tags) {
        this.tags = tags;
    }
    
    public void updateHint(String hint) {
        this.hint = hint;
    }
    
    public void updateSource(String source) {
        this.source = source;
    }

    public void updateTestcaseFilePath(String testcaseFilePath) {
        this.testcaseFilePath = testcaseFilePath;
    }
}
