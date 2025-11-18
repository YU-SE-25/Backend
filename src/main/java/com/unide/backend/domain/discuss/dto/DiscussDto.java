package com.unide.backend.domain.discuss.dto;

import com.unide.backend.domain.discuss.entity.Discuss;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함하는 생성자
public class DiscussDto {

    private Long postId;
    private Long authorId;
    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
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