package com.unide.backend.domain.studygroup.discuss.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class st_DiscussLikeId implements Serializable {

    private Long postId;
    private Long likerId;
    private Long groupId;
}
