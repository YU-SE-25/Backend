// 소셜 로그인 성공 후 JWT를 생성하고 프론트엔드로 리다이렉트시키는 핸들러

package com.unide.backend.global.security.oauth;

import com.unide.backend.domain.auth.entity.RefreshToken;
import com.unide.backend.domain.auth.repository.RefreshTokenRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.global.security.jwt.JwtTokenProvider;
import com.unide.backend.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        user.onLoginSuccess();
        userRepository.save(user);

        // 액세스 토큰과 리프레시 토큰을 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user);

        // 리프레시 토큰을 DB에 저장하거나 업데이트
        final LocalDateTime tokenExpires = LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshExpirationMs() / 1000);
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshTokenValue, tokenExpires),
                        () -> {
                            RefreshToken newRefreshToken = RefreshToken.builder()
                                    .user(user)
                                    .tokenValue(refreshTokenValue)
                                    .expiresAt(tokenExpires)
                                    .build();
                            refreshTokenRepository.save(newRefreshToken);
                        }
                );

        // 프론트엔드로 리다이렉트할 URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshTokenValue)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
