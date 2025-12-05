package com.unide.backend.domain.mainpage.repository;

public interface CodeReviewRankProjection {

    Long getId();          // code_review id
    Long getAuthorId();    // 작성자 id
    String getNickname();  // ⭐ 작성자 닉네임
    Integer getVote();     // 투표수
}
