package com.unide.backend.domain.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.mypage.entity.Goals;

public interface GoalsRepository extends JpaRepository<Goals, Long> {
    Goals findByUserId(Long userId);
}