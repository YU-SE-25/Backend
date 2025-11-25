package com.unide.backend.domain.discuss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.discuss.entity.Discuss;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;

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

    // 첨부파일 URL
    private String attachmentUrl;

    // 현재 사용자 기준 좋아요 여부
    @JsonProperty("viewerLiked")
    private boolean viewerLiked;

    /* ===================== 정적 팩토리 메서드 ===================== */

    /** 기본 변환 */
    public static DiscussDto fromEntity(Discuss entity) {
        return fromEntity(entity, false);
    }

    /** viewerLiked 포함 변환 */
    public static DiscussDto fromEntity(Discuss entity, boolean viewerLiked) {

        if (entity == null) return null;

        return DiscussDto.builder()
                .postId(entity.getPostId())
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
