package com.unide.backend.domain.studygroup.discuss.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class st_DiscussCommentLikeId implements Serializable {
    private Long commentId;
    private Long likerId;
    private Long groupId;
}
