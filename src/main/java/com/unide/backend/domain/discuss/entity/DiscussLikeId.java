package com.unide.backend.domain.discuss.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class DiscussLikeId implements Serializable {
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "liker_id")
    private Long likerId;
}
