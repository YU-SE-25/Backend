package com.unide.backend.domain.qna.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "QnA_like")
public class QnALike {

    @EmbeddedId
    private QnALikeId id;

    // DB에서 DEFAULT CURRENT_TIMESTAMP 로 넣어주니까 insertable=false 로 둬도 됨
    @Column(name = "liked_at", insertable = false, updatable = false)
    private LocalDateTime likedAt;

    // 편하게 만들려고 하는 팩토리 메서드
    public static QnALike of(Long postId, Long likerId) {
        return QnALike.builder()
                .id(new QnALikeId(postId, likerId))
                .build();
    }
}
