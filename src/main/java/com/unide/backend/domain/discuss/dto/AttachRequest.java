package com.unide.backend.domain.discuss.dto;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachRequest {
    private Long postId;
    private String contents;   // 첨부파일 url
}