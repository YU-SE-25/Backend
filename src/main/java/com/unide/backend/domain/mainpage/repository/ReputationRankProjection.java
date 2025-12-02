package com.unide.backend.domain.mainpage.repository;

public interface ReputationRankProjection {
    Long getUserId();
    Integer getRanking();
    Integer getDelta();
    Integer getRating();  // ⭐ 추가!
}
