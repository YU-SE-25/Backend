// 다양한 OAuth2 제공자(Google, Kakao 등)의 사용자 정보를 표준화하는 

package com.unide.backend.global.security.oauth;

import java.util.Map;

public interface OAuth2UserInfo {
    Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
