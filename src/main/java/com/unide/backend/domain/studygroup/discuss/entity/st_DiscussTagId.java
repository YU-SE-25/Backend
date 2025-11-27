package com.unide.backend.domain.studygroup.discuss.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class st_DiscussTagId implements Serializable {

    private Long postId;
    private Long tagId;
    private Long groupId;

    public st_DiscussTagId(Long postId, Long tagId, Long groupId) {
        this.postId = postId;
        this.tagId = tagId;
        this.groupId = groupId;
    }
}
