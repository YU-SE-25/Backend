// 파일 업로드 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.common.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponseDto {
    private String fileUrl;
    private String originalFileName;
    private Long fileSize;
}
