package com.unide.backend.domain.studygroup.discuss.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(st_DiscussTagId.class)
@Table(name = "group_post_tag")
public class st_DiscussTag {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    @Id
    @Column(name = "group_id")
    private Long groupId;

    public st_DiscussTag(Long postId, Long tagId, Long groupId, boolean dummy) {
        this.postId = postId;
        this.tagId = tagId;
        this.groupId = groupId;
    }
}
