// 리프레시 토큰 엔티티에 대한 데이터베이스 접근을 처리하는 JPA 레포지토리 인터페이스

package com.unide.backend.domain.auth.repository;

import com.unide.backend.domain.auth.entity.RefreshToken;
import com.unide.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 토큰 값으로 엔터티를 찾기 위해 사용 
    Optional<RefreshToken> findByTokenValue(String tokenValue);
    
    // 사용자 ID로 기존 토큰이 있는지 확인하기 위해 사용
    Optional<RefreshToken> findByUserId(Long userId);
    
    // 로그아웃 시 토큰을 삭제하기 위해 사용
    void deleteByUser(User user);

    // 특정 사용자의 모든 리프레시 토큰 삭제
    void deleteByUserId(Long userId);
}
