package com.unide.backend.domain.discuss.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "post_tag")
@IdClass(DiscussTagId.class)
@Getter
@Setter
@NoArgsConstructor
public class DiscussTag implements Serializable {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    // 편의 생성자
    public DiscussTag(Long postId, Long tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}
