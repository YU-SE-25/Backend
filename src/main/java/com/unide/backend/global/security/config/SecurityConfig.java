// Spring Security의 핵심 설정을 담당하는 클래스

package com.unide.backend.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF 보호 비활성화
            .csrf(csrf -> csrf.disable())
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )

            // HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
                // Swagger UI 관련 경로는 누구나 접근 가능하도록 허용
                .requestMatchers(SWAGGER_URL_PATTERNS).permitAll()
                // 인증 관련 경로는 누구나 접근 가능하도록 허용
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                // 문제 목록 조회와 상세 조회는 누구나 접근 가능
                .requestMatchers("GET", "/api/problems/list").permitAll()
                .requestMatchers("GET", "/api/problems/detail/**").permitAll()
                // 포트폴리오 업로드 경로는 누구나 접근 가능하도록 허용
                .requestMatchers("/api/upload/portfolio").permitAll()
                // 닉네임으로 마이페이지 조회는 누구나 접근 가능 (GET만 허용)
                .requestMatchers("GET", "/api/mypage/{nickname}").permitAll()
                // 나머지 stats/me, goals/me 등은 인증 필요
                .requestMatchers("/api/mypage/stats/**").permitAll()
                .requestMatchers("/api/mypage/goals/**").authenticated()
                // MANAGER 역할을 가진 사용자만 접근 가능
                .requestMatchers("/api/admin/**").hasRole("MANAGER")
                //메인화면 모두 조회 가능
                .requestMatchers("/api/UNIDE/rank/**").permitAll()

                // 마이페이지 수정/삭제는 인증 필요 (PATCH, DELETE)
                .requestMatchers("PATCH", "/api/mypage/me").authenticated()
                .requestMatchers("DELETE", "/api/mypage/me").authenticated()
                
                // 2) 스터디 그룹 목록 조회만 오픈
                 .requestMatchers(HttpMethod.GET, "/api/studygroup/**").permitAll()

                 // 3) 나머지 스터디그룹 관련 기능은 로그인 필수
                .requestMatchers("/api/studygroup/**").authenticated()
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:5173"); 
        configuration.addAllowedOrigin("http://localhost:3000");

        configuration.addAllowedMethod("*"); 
        
        configuration.addAllowedHeader("*"); 
        
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
