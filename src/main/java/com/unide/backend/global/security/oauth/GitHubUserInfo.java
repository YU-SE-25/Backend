// GitHub이 제공하는 사용자 정보를 파싱하여 OAuth2UserInfo 인터페이스에 맞게 변환하는 클래스

package com.unide.backend.global.security.oauth;

import java.util.Map;

public class GitHubUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GitHubUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        return name != null ? name : (String) attributes.get("login");
    }
}
