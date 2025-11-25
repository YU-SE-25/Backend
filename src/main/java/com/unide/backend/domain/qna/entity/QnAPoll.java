package com.unide.backend.domain.qna.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "qna_poll")
public class QnAPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_private", nullable = false)
    private boolean privatePoll;

    @Column(name = "allows_multi", nullable = false)
    private boolean allowsMulti;
    
    @Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt = LocalDateTime.now();




    protected QnAPoll() {}

    public QnAPoll(Long postId,
                   Long authorId,
                   String title,
                   LocalDateTime endTime,
                   boolean privatePoll,
                   boolean allowsMulti) {
        this.postId = postId;
        this.authorId = authorId;
        this.title = title;
        this.endTime = endTime;
        this.privatePoll = privatePoll;
        this.allowsMulti = allowsMulti;
        this.createdAt = LocalDateTime.now();
    }

    // getter만 필요하면 setter는 안 만들어도 됨
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public Long getAuthorId() { return authorId; }
    public String getTitle() { return title; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isPrivatePoll() { return privatePoll; }
    public boolean isAllowsMulti() { return allowsMulti; }
    public LocalDateTime getCreatedAt() {
    return createdAt;
}
}
