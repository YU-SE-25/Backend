package com.unide.backend.domain.discuss.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscussCommentLikeId implements Serializable {
    private Long commentId;
    private Long likerId;
}
