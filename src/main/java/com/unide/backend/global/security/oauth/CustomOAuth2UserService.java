// 소셜 로그인 성공 후 사용자 정보를 처리하는 핵심 서비스

package com.unide.backend.global.security.oauth;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.global.security.oauth.GitHubUserInfo;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2UserService를 통해 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 소셜인지 구분
        final OAuth2UserInfo oAuth2UserInfo;
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if (provider.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("github")) {
            oAuth2UserInfo = new GitHubUserInfo(oAuth2User.getAttributes());
        } else {
            // 다른 소셜 서비스
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        String email = oAuth2UserInfo.getEmail();

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // 이메일로 기존 회원 조회
        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // 이미 존재하는 사용자인 경우, 소셜 계정 정보 업데이트
                    return existingUser;
                })
                .orElseGet(() -> {
                    String baseNickname = oAuth2UserInfo.getName();

                    if (baseNickname == null || baseNickname.isEmpty()) {
                        baseNickname = "User";
                    }

                    String uniqueNickname = baseNickname;

                    while (userRepository.existsByNickname(uniqueNickname)) {
                        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
                        uniqueNickname = baseNickname + "_" + randomSuffix;
                    }

                    String tempPhone = "social_" + provider + "_" + oAuth2UserInfo.getProviderId();

                    // 존재하지 않는 사용자인 경우, 회원가입 처리
                    User newUser = User.builder()
                            .email(email)
                            .name(oAuth2UserInfo.getName())
                            .nickname(uniqueNickname) // 닉네임은 임시로 이름으로 설정
                            .phone(tempPhone) // 소셜 로그인은 휴대폰 번호를 알 수 없으므로 임시 값 설정
                            .role(UserRole.LEARNER)
                            .build();
                    newUser.activateAccount(); // 소셜 로그인은 이메일이 검증된 것으로 간주하여 바로 활성화
                    newUser.markAsSocialAccount();
                    return userRepository.save(newUser);
                });

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
