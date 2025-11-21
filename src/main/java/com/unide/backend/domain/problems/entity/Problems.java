package com.unide.backend.domain.problems.entity;

import java.util.ArrayList;
import java.util.List;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private ProblemVisibility visibility;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ProblemTag> tags = new ArrayList<>();

    @Lob
    private String hint;

    @Column(length = 100)
    private String source;

    @Builder
    public Problems(User createdBy, String title, String description, String inputOutputExample, ProblemDifficulty difficulty,
                    Integer timeLimit, Integer memoryLimit, ProblemVisibility visibility, List<ProblemTag> tags,
                    String hint, String source) {
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.inputOutputExample = inputOutputExample;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.visibility = (visibility != null) ? visibility : ProblemVisibility.PUBLIC;
        this.viewCount = 0;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        this.hint = hint;
        this.source = source;
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
    
    public void updateVisibility(ProblemVisibility visibility) {
        this.visibility = visibility;
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
}
