package com.unide.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 업로드된 파일(아바타 / 테스트케이스 등)을
 * 프론트엔드에서 URL로 접근할 수 있게 해주는 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** application.yml 설정에서 주입됨 */
    @Value("${app.upload.avatar-dir}")
    private String avatarDir;

    @Value("${app.upload.testcase-dir}")
    private String testcaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 📌 1. 아바타 이미지 서빙
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + avatarDir + "/")
                .setCachePeriod(3600); // 캐싱(선택)

        // 📌 2. 테스트케이스 파일 서빙
        registry.addResourceHandler("/uploads/testcases/**")
                .addResourceLocations("file:" + testcaseDir + "/")
                .setCachePeriod(3600);

        // (선택) 공통 uploads 경로 전체 서빙
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
