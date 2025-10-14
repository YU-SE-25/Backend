// 사용자 정보를 데이터베이스에서 관리하기 위한 JPA 레포지토리 인터페이스

package com.unide.backend.domain.user.repository;

import com.unide.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일이 존재하는지 확인하는 메서드
    boolean existsByEmail(String email);

    // 닉네임이 존재하는지 확인하는 메서드
    boolean existsByNickname(String nickname);
}
