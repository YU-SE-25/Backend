package com.unide.backend.domain.discuss.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discuss")
public class Discuss extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "contents", columnDefinition = "TEXT", nullable = false)
    private String contents;

    @Column(name = "tag", length = 50)
    private String tag;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    private int commentCount;

    // ============================
    //     업데이트 메서드
    // ============================
    public void update(
            String newTitle,
            String newContents,
            String newTag,
            boolean newPrivate,
            boolean anonymity
    ) {
        this.title = newTitle;
        this.contents = newContents;
        this.tag = newTag;
        this.isPrivate = newPrivate;
        this.isAnonymous = anonymity;
    }

    // Builder boolean setter alias
    public void anonymous(boolean value) {
        this.isAnonymous = value;
    }

    // DiscussService가 사용하는 alias
    public boolean isAnonymity() {
        return this.isAnonymous;
    }
}
