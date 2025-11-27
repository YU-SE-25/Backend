package com.unide.backend.domain.studygroup.problem.entity;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_group_problem_list")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupProblemList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_list_id")
    private Long id;   // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @Column(name = "list_title", length = 100, nullable = false)
    private String listTitle;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "problemList", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyGroupProblem> problems = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
