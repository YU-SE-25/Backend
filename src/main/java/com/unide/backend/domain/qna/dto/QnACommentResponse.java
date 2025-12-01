package com.unide.backend.domain.qna.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.qna.entity.QnAComment;

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
public class QnACommentResponse {
    
    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("author_id")
    private Long authorId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("anonymity")
    private boolean anonymity;

    @JsonProperty("content")
    private String content;

    @JsonProperty("is_private")
    private boolean privatePost;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("viewerLiked")
    private boolean viewerLiked;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;   // ✅ 여기
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;   // ✅ 여기

    @JsonProperty("message")
    private String message;

    public static QnACommentResponse fromEntity(
            QnAComment c,
            boolean viewerLiked,
            String message,
            String authorName
    ) {
        if (c == null) return null;

        return QnACommentResponse.builder()
                .commentId(c.getCommentId())
                .postId(c.getPostId())
                .parentId(c.getParentCommentId())
                .authorId(c.getAuthorId())
                .authorName(authorName)
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
