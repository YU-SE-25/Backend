package com.unide.backend.domain.studygroup.discuss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.studygroup.discuss.entity.st_Discuss;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class st_DiscussDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long postId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long groupId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;

    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int likeCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int commentCount;

    private String attachmentUrl;

    @JsonProperty("viewerLiked")
    private boolean viewerLiked;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;

    /* ===================== 정적 팩토리 메서드 ===================== */

    /** 기본 변환 */
    public static st_DiscussDto fromEntity(st_Discuss entity) {
        return fromEntity(entity, false);
    }

    /** viewerLiked 포함 변환 */
    public static st_DiscussDto fromEntity(st_Discuss entity, boolean viewerLiked) {

        if (entity == null) return null;

        return st_DiscussDto.builder()
                .postId(entity.getPostId())
                .groupId(entity.getGroupId())
                .authorId(entity.getAuthorId())
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
}
