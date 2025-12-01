package com.unide.backend.domain.discuss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.discuss.entity.Discuss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;

    // ⭐ 추가: 작성자 이름
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String authorName;

    private Long postId;
    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int likeCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int commentCount;

    private String attachmentUrl;

    @JsonProperty("viewerLiked")
    private boolean viewerLiked;

    /* ===================== 정적 팩토리 메서드 ===================== */

    public static DiscussDto fromEntity(Discuss entity, String authorName, boolean viewerLiked) {
        if (entity == null) return null;

        return DiscussDto.builder()
                .postId(entity.getPostId())
                .authorId(entity.getAuthorId())
                .authorName(authorName)
                .anonymous(entity.isAnonymous())
                .title(entity.getTitle())
                .contents(entity.getContents())
                .privatePost(entity.isPrivatePost())
                .likeCount(entity.getLikeCount())
                .commentCount(entity.getCommentCount())
                .attachmentUrl(entity.getAttachmentUrl())
                .viewerLiked(viewerLiked)
                .build();
    }

    public static DiscussDto fromEntity(Discuss entity, String authorName) {
        return fromEntity(entity, authorName, false);
    }
}
