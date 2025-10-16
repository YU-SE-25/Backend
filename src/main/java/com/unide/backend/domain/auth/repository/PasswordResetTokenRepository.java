// PasswordResetToken 엔티티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.auth.repository;

import com.unide.backend.domain.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser_EmailAndVerificationCode(String email, String code);
}
