package com.unide.backend.domain.mainpage.repository;

public interface ReputationRankProjection {
    Long getUserId();
    String getNickname();   // ⭐ 반드시 추가
    Integer getRanking();
    Integer getDelta();
    Integer getRating();
}
