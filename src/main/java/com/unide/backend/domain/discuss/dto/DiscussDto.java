package com.unide.backend.domain.discuss.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.discuss.entity.Discuss;

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

public class DiscussDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;
    private Long postId;
    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;
    

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int likeCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int commentCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    private String attachmentUrl; // ⭐ 추가된 부분

    public static DiscussDto fromEntity(Discuss discuss) {
        if (discuss == null) return null;

        return DiscussDto.builder()
                .postId(discuss.getPostId())
                .authorId(discuss.getAuthorId())
                .anonymous(discuss.isAnonymous())
                .title(discuss.getTitle())
                .contents(discuss.getContents())
                .privatePost(discuss.isPrivatePost())
                .likeCount(discuss.getLikeCount())
                .commentCount(discuss.getCommentCount())
                .createdAt(discuss.getCreatedAt())
                .updatedAt(discuss.getUpdatedAt())
                .attachmentUrl(discuss.getAttachmentUrl()) // ⭐ 여기도 추가
                .build();
    }
}
