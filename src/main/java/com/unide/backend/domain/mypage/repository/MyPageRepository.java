package com.unide.backend.domain.mypage.repository;

import com.unide.backend.domain.mypage.entity.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPageRepository extends JpaRepository<MyPage, Long> {
    Optional<MyPage> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByNicknameAndUserIdNot(String nickname, Long userId);
    Optional<MyPage> findByNickname(String nickname);
}
