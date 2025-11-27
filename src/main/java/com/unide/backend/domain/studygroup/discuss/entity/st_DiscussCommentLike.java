package com.unide.backend.domain.studygroup.discuss.entity;

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
@Table(name="group_dis_post_commet_like")
@IdClass(st_DiscussCommentLikeId.class)
public class st_DiscussCommentLike {
    @Id
    @Column(name = "comment_id")
    private Long commentId;

    @Id
    @Column(name = "liker_id")
    private Long likerId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void onCreate() {
        this.likedAt = LocalDateTime.now();
    }

}
