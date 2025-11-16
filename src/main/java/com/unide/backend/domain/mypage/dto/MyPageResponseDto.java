// 마이페이지 통합 조회 응답 DTO
// User 정보 + MyPage 정보 + 통계 + 제출 내역을 한 번에 반환

package com.unide.backend.domain.mypage.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyPageResponseDto {
    // 사용자 기본 정보
    private final Long userId;
    private final String nickname;
    private final String avatarUrl;
    private final String bio;
    private final List<String> preferredLanguage;
    private final String role;
    private final LocalDateTime joinedAt;
    private final Boolean isPublic;
    
    // 문제 풀이 관련
    private final List<Long> solvedProblems;
    private final List<Long> bookmarkedProblems;
    
    // 최근 제출 내역
    private final List<SubmissionItem> recentSubmissions;
    
    // 통계
    private final UserStats stats;
}
