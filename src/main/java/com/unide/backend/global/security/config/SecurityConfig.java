// Spring Security의 핵심 설정을 담당하는 클래스

package com.unide.backend.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.unide.backend.global.security.jwt.JwtAuthenticationFilter;
import com.unide.backend.global.security.oauth.CustomOAuth2UserService;
import com.unide.backend.global.security.oauth.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // Swagger UI와 API 문서에 대한 경로
    private static final String[] SWAGGER_URL_PATTERNS = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 비활성화
            .csrf(csrf -> csrf.disable())
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
                // Swagger UI 관련 경로는 누구나 접근 가능하도록 허용
                .requestMatchers(SWAGGER_URL_PATTERNS).permitAll()
                // 인증 관련 경로는 누구나 접근 가능하도록 허용
                .requestMatchers("/api/auth/**").permitAll()
                // 포트폴리오 업로드 경로는 누구나 접근 가능하도록 허용
                .requestMatchers("/api/upload/portfolio").permitAll()
                // MANAGER 역할을 가진 사용자만 접근 가능
                .requestMatchers("/api/admin/**").hasRole("MANAGER")
                // 나머지 모든 요청은 일단 인증된 사용자만 접근 가능하도록 설정
                .anyRequest().authenticated()
            )

            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
