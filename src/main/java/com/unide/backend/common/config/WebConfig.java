package com.unide.backend.common.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.avatar-dir}")      // 예: uploads/avatars
    private String avatarDir;

    @Value("${app.upload.testcase-dir}")    // 예: uploads/testcases
    private String testcaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 실제 절대 경로 계산
        String avatarAbsolute = Paths.get(avatarDir).toAbsolutePath().toUri().toString();
        String testcaseAbsolute = Paths.get(testcaseDir).toAbsolutePath().toUri().toString();

        /**
         * ================================
         * 1. 프로필 이미지 서빙
         * URL 예: /uploads/profile-images/xxx.png
         *
         * 실제 경로: uploads/avatars/profile-images/
         * ================================
         */
        String profileImagesAbsolute = Paths.get(avatarDir, "profile-images")
                .toAbsolutePath()
                .toUri()
                .toString();

        registry.addResourceHandler("/uploads/profile-images/**")
                .addResourceLocations(profileImagesAbsolute)
                .setCachePeriod(3600);


        /**
         * ================================
         * 2. avatars 폴더 전체 매핑
         * URL: /uploads/avatars/**
         * 실제 경로: uploads/avatars/
         * ================================
         */
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations(avatarAbsolute)
                .setCachePeriod(3600);


        /**
         * ================================
         * 3. 테스트케이스 파일 매핑
         * URL: /uploads/testcases/**
         * 실제 경로: uploads/testcases/
         * ================================
         */
        registry.addResourceHandler("/uploads/testcases/**")
                .addResourceLocations(testcaseAbsolute)
                .setCachePeriod(3600);


        /**
         * ================================
         * 4. 공통 uploads 경로
         * URL: /uploads/**
         * 실제 경로: uploads/
         * ================================
         */
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);
    }
}
