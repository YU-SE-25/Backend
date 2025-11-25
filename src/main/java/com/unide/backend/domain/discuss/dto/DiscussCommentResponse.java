package com.unide.backend.domain.discuss.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.discuss.entity.DiscussComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussCommentResponse {

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("anonymity")
    private boolean anonymity;

    @JsonProperty("content")
    private String content;

    @JsonProperty("is_private")
    private boolean privatePost;

    @JsonProperty("like_count")
    private int likeCount;

    // 현재 사용자 기준 좋아요 여부
    @JsonProperty("viewerLiked")
    private boolean viewerLiked;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // 메세지 (등록/수정/삭제 등)
    @JsonProperty("message")
    private String message;

    public static DiscussCommentResponse fromEntity(DiscussComment c,
                                                    boolean viewerLiked,
                                                    String message) {
        if (c == null) return null;

        return DiscussCommentResponse.builder()
                .commentId(c.getCommentId())
                .postId(c.getPostId())
                .parentId(c.getParentCommentId())
                .authorId(c.getAuthorId())
                .anonymity(c.isAnonymous())
                .content(c.getContent())
                .privatePost(c.isPrivatePost())
                .likeCount(c.getLikeCount())
                .viewerLiked(viewerLiked)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .message(message)
                .build();
    }
}
