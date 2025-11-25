package com.unide.backend.domain.discuss.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dis_comment_like")
@IdClass(DiscussCommentLikeId.class)
public class DiscussCommentLike {

    @Id
    @Column(name = "comment_id")
    private Long commentId;

    @Id
    @Column(name = "liker_id")
    private Long likerId;

    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void onCreate() {
        this.likedAt = LocalDateTime.now();
    }
}
