// Blacklist 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.admin.repository;

import com.unide.backend.domain.admin.entity.Blacklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    @Query("SELECT COUNT(b) > 0 FROM Blacklist b WHERE b.email = :email OR b.phone = :phone")
    boolean existsByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);
}
