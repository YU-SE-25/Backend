package com.unide.backend.domain.mypage.repository;

import com.unide.backend.domain.mypage.entity.Stats;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    Stats findByUserId(Long userId);
}