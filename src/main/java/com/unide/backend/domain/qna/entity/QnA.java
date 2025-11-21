package com.unide.backend.domain.qna.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "qna")
public class QnA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;          // PK

    @Column(name = "author_id", nullable = false)
    private Long authorId;        // FK → users.id (지금은 Long으로만 둠)

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous;    // 익명 여부

    @Column(name = "title", nullable = false, length = 100)
    private String title;         // 제목

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;      // 내용

    @Column(name = "is_private", nullable = false)
    private boolean privatePost;  // 비공개 여부

    @Column(name = "like_count", nullable = false)
    private int likeCount;        // 좋아요 수

    @Column(name = "comment_count", nullable = false)
    private int commentCount;     // 댓글 수

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 작성 시간

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정 시간

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
