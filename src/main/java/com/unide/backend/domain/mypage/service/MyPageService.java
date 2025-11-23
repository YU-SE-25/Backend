package com.unide.backend.domain.mypage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unide.backend.domain.mypage.dto.MyPageResponseDto;
import com.unide.backend.domain.mypage.dto.MyPageUpdateRequestDto;
import com.unide.backend.domain.mypage.dto.SubmissionItem;
import com.unide.backend.domain.mypage.dto.UserStats;
import com.unide.backend.domain.mypage.entity.MyPage;
import com.unide.backend.domain.mypage.repository.MyPageRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MyPageRepository myPageRepository;
    private final UserRepository userRepository;
    private final SubmissionsRepository submissionsRepository;
    private final ObjectMapper objectMapper;
    
    private static final String DEFAULT_AVATAR_URL = "/images/default-avatar.png";

    public MyPageResponseDto getMyPage(Long userId) {
        User user = getUserById(userId);
        MyPage myPage = myPageRepository.findByUserId(userId).orElse(null);
        return buildMyPageResponse(user, myPage);
    }

    public MyPageResponseDto getMyPageByNickname(String nickname, Long requestUserId) {
        MyPage myPage = myPageRepository.findByNickname(nickname).orElse(null);
        
        User user = (myPage != null) ? myPage.getUser() 
                : userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        if (myPage == null) {
            myPage = myPageRepository.findByUserId(user.getId()).orElse(null);
        }
        
        validateProfileAccess(myPage, user.getId(), requestUserId);
        return buildMyPageResponse(user, myPage);
    }

    private MyPageResponseDto buildMyPageResponse(User user, MyPage myPage) {
        Long userId = user.getId();

        List<Long> solvedProblems = submissionsRepository.findSolvedProblemsByUserId(userId);
        List<SubmissionItem> recentSubmissions = getRecentSubmissions(userId);
        UserStats stats = calculateUserStats(userId);
        List<String> preferredLanguageList = parsePreferredLanguages(myPage);

        return MyPageResponseDto.builder()
                .userId(user.getId())
                .nickname(getNickname(myPage, user))
                .avatarUrl(getAvatarUrl(myPage))
                .bio(myPage != null ? myPage.getBio() : null)
                .preferredLanguage(preferredLanguageList)
                .role(user.getRole().toString())
                .joinedAt(user.getCreatedAt())
                .updatedAt(myPage != null ? myPage.getUpdatedAt() : user.getUpdatedAt())
                .isPublic(myPage != null ? myPage.getIsPublic() : true)
                .solvedProblems(solvedProblems)
                .bookmarkedProblems(List.of())
                .recentSubmissions(recentSubmissions)
                .stats(stats)
                .build();
    }

    @Transactional
    public MyPageResponseDto createMyPage(Long userId) {
        User user = getUserById(userId);

        if (myPageRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 마이페이지가 존재합니다.");
        }

        MyPage myPage = createDefaultMyPage(user);
        myPageRepository.save(myPage);
        return buildMyPageResponse(user, myPage);
    }

    @Transactional
    public MyPageResponseDto updateMyPage(Long userId, MyPageUpdateRequestDto requestDto) {
        User user = getUserById(userId);
        MyPage myPage = getMyPageByUserId(userId);

        updateNicknameIfPresent(requestDto.getNickname(), userId, user, myPage);
        updateBioIfPresent(requestDto.getBio(), myPage);
        updatePreferredLanguageIfPresent(requestDto.getPreferredLanguage(), myPage);
        updatePublicStatusIfPresent(requestDto.getIsPublic(), myPage);

        userRepository.save(user);
        myPageRepository.save(myPage);
        return buildMyPageResponse(user, myPage);
    }

    @Transactional
    public void deleteMyPage(Long userId) {
        MyPage myPage = getMyPageByUserId(userId);
        myPageRepository.delete(myPage);
    }

    @Transactional
    public MyPageResponseDto initializeMyPage(Long userId) {
        User user = getUserById(userId);
        
        myPageRepository.findByUserId(userId).ifPresent(existingMyPage -> {
            myPageRepository.delete(existingMyPage);
            myPageRepository.flush();
        });
        
        MyPage newMyPage = createDefaultMyPage(user);
        myPageRepository.save(newMyPage);
        return buildMyPageResponse(user, newMyPage);
    }

    // ========== Private Helper Methods ==========

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private MyPage getMyPageByUserId(Long userId) {
        return myPageRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("마이페이지를 찾을 수 없습니다."));
    }

    private void validateProfileAccess(MyPage myPage, Long ownerId, Long requestUserId) {
        if (myPage != null && !myPage.getIsPublic() && 
            (requestUserId == null || !requestUserId.equals(ownerId))) {
            throw new IllegalArgumentException("비공개 프로필입니다.");
        }
    }

    private List<SubmissionItem> getRecentSubmissions(Long userId) {
        return submissionsRepository
                .findRecentSubmissionsByUserId(userId, PageRequest.of(0, 5))
                .stream()
                .map(sub -> new SubmissionItem(
                        sub.getId(),
                        sub.getProblem().getId(),
                        sub.getStatus().name(),
                        sub.getLanguage().name(),
                        sub.getRuntime() != null ? sub.getRuntime() : 0,
                        sub.getSubmittedAt()
                ))
                .collect(Collectors.toList());
    }

    private UserStats calculateUserStats(Long userId) {
        Long totalSubmitted = submissionsRepository.countByUserId(userId);
        Long totalSolved = submissionsRepository.countSolvedByUserId(userId);
        double acceptanceRate = totalSubmitted > 0 ? (totalSolved * 100.0 / totalSubmitted) : 0.0;

        return new UserStats(
                totalSolved != null ? totalSolved.intValue() : 0,
                totalSubmitted != null ? totalSubmitted.intValue() : 0,
                Math.round(acceptanceRate * 10.0) / 10.0,
                0, 0, 0
        );
    }

    private List<String> parsePreferredLanguages(MyPage myPage) {
        if (myPage == null || myPage.getPreferredLanguage() == null) {
            return List.of();
        }
        
        try {
            return objectMapper.readValue(
                    myPage.getPreferredLanguage(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private String getNickname(MyPage myPage, User user) {
        return (myPage != null && myPage.getNickname() != null) 
                ? myPage.getNickname() 
                : user.getNickname();
    }

    private String getAvatarUrl(MyPage myPage) {
        return (myPage != null && myPage.getAvatarUrl() != null) 
                ? myPage.getAvatarUrl() 
                : DEFAULT_AVATAR_URL;
    }

    private MyPage createDefaultMyPage(User user) {
        return MyPage.builder()
                .userId(user.getId())
                .user(user)
                .nickname(user.getNickname())
                .isPublic(true)
                .build();
    }

    private void updateNicknameIfPresent(String nickname, Long userId, User user, MyPage myPage) {
        if (nickname == null) {
            return;
        }
        
        if (userRepository.existsByNicknameAndIdNot(nickname, userId) ||
            myPageRepository.existsByNicknameAndUserIdNot(nickname, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        
        myPage.updateNickname(nickname);
        user.updateNickname(nickname);
    }

    private void updateBioIfPresent(String bio, MyPage myPage) {
        if (bio != null) {
            myPage.updateBio(bio);
        }
    }

    private void updatePreferredLanguageIfPresent(List<String> preferredLanguage, MyPage myPage) {
        if (preferredLanguage == null) {
            return;
        }
        
        try {
            String jsonLanguage = objectMapper.writeValueAsString(preferredLanguage);
            myPage.updatePreferredLanguage(jsonLanguage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("선호 언어 JSON 변환 실패", e);
        }
    }

    private void updatePublicStatusIfPresent(Boolean isPublic, MyPage myPage) {
        if (isPublic != null) {
            myPage.updateIsPublic(isPublic);
        }
    }
}
