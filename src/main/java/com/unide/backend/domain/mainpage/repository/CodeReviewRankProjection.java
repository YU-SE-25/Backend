// src/main/java/com/unide/backend/domain/mainpage/repository/CodeReviewRankProjection.java
package com.unide.backend.domain.mainpage.repository;

public interface CodeReviewRankProjection {

    Long getId();         // 리뷰 id
    Long getAuthorId();   // reviewer_id
    Integer getVote();    // vote_count
}
