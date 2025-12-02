package com.unide.backend.domain.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.mypage.entity.Stats;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    Stats findByUserId(Long userId);
     // 점수 순으로 정렬해서 랭킹 계산용
    List<Stats> findAllByOrderByRatingDesc();
}