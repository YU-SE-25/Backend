package com.unide.backend.domain.discuss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.discuss.entity.Discuss;
import lombok.*;
import java.time.LocalDateTime;



@Getter
@Setter
@Builder
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함하는 생성자
public class DiscussDto {
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)   // 🔒 요청에서 들어오는 값은 무시
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

    // DTO 내부에서 Entity로 변환하는 메서드를 Builder 패턴으로 구현
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
                .build();
    }
}