package com.unide.backend.domain.mypage.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    /** 프로필 이미지 업로드 */
    String uploadProfileImage(MultipartFile file);

    /** URL 기반으로 실제 파일 삭제 */
    void deleteImage(String imageUrl);
}
