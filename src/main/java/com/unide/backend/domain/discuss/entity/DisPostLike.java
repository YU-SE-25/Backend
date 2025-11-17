package com.unide.backend.domain.discuss.entity;

import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dis_post_like")
public class DisPostLike {

    @EmbeddedId
    private DisPostLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Discuss post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("likerId")
    @JoinColumn(name = "liker_id")
    private User liker;
}
