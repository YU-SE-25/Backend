// JWT 토큰 생성, 파싱 및 유효성 검사를 담당하는 유틸리티 클래스

package com.unide.backend.global.security.jwt;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
        @Value("${spring.jwt.secret-key}") String secretKey,
        @Value("${spring.jwt.access-token-expiration-ms}") long accessExpirationMs,
        @Value("${spring.jwt.refresh-token-expiration-ms}") long refreshExpirationMs) {

        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // 액세스 토큰 생성
    public String createAccessToken(User user) {
        return createToken(user, accessExpirationMs);
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(User user) {
        return createToken(user, refreshExpirationMs);
    }
    
    // 토큰 생성 공통 메서드
    private String createToken(User user, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        return Jwts.builder()
                .setSubject(user.getId().toString()) // 토큰의 주체: 사용자 ID
                .claim("role", user.getRole().toString()) // 사용자 역할 정보 포함
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.");
        }
        return false;
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return Long.parseLong(claims.getSubject());
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
