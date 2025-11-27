package com.unide.backend.domain.studygroup.discuss.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@Table(name = "group_dis_post_like")
public class st_DiscussLike {

    @EmbeddedId
    private st_DiscussLikeId id;

    // DB에서 DEFAULT CURRENT_TIMESTAMP 쓰니까 insertable=false, updatable=false 그대로 OK
    @Column(name = "liked_at", insertable = false, updatable = false)
    private LocalDateTime likedAt;

    // ❌ 이 필드 때문에 group_id가 두 번 매핑됐었음 → 제거
    // @Column(name = "group_id", nullable = false)
    // private Long groupId;

    // 편의를 위한 getter (원하면)
    @Transient
    public Long getGroupId() {
        return (id != null) ? id.getGroupId() : null;
    }

    // 팩토리 메서드
    public static st_DiscussLike of(Long postId, Long likerId, Long groupId) {
        return st_DiscussLike.builder()
                .id(new st_DiscussLikeId(postId, likerId, groupId))
                .build();
    }
}
