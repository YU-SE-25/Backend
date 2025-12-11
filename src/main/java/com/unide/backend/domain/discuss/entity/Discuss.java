package com.unide.backend.domain.discuss.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList; 
import java.util.List; 
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.Builder.Default;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "discuss")
public class Discuss {

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

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;//url

    @Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscussReport> reports = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
     // ⭐ 수동으로 추가
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
}
