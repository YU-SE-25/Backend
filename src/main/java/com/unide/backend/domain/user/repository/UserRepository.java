// 사용자 정보를 데이터베이스에서 관리하기 위한 JPA 레포지토리 인터페이스

package com.unide.backend.domain.user.repository;

import com.unide.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
}
