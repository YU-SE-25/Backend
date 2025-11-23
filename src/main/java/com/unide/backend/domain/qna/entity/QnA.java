package com.unide.backend.domain.qna.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "qna")
public class QnA extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;          // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;      // 작성자

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous;    // 익명 여부

    @Column(name = "title", nullable = false, length = 100)
    private String title;         // 제목

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;      // 내용

    @Column(name = "is_private", nullable = false)
    private boolean privatePost;  // 비공개 여부

    @Column(name = "like_count", nullable = false)
    private int likeCount;        // 좋아요 수

    @Column(name = "comment_count", nullable = false)
    private int commentCount;     // 댓글 수
}
