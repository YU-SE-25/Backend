// 사용자 정보를 데이터베이스에서 관리하기 위한 JPA 레포지토리 인터페이스

package com.unide.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일이 존재하는지 확인하는 메서드
    boolean existsByEmail(String email);

    // 닉네임이 존재하는지 확인하는 메서드
    boolean existsByNickname(String nickname);

    // 휴대폰 번호가 존재하는지 확인하는 메서드
    boolean existsByPhone(String phone);

    // 이메일로 사용자를 찾는 메서드
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자를 찾는 메서드
    Optional<User> findByNickname(String nickname);

    // 특정 ID를 제외하고 닉네임이 존재하는지 확인하는 메서드
    boolean existsByNicknameAndIdNot(String nickname, Long id);

    // 특정 역할이 아닌 사용자 수를 세는 메서드
    long countByRoleNot(UserRole role);

    // 휴대폰 번호로 사용자를 찾는 메서드
    Optional<User> findByPhone(String phone);
}
