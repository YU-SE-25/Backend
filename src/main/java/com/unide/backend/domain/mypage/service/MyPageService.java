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
import com.unide.backend.domain.submission.repository.SubmissionRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MyPageRepository myPageRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;
    
    private static final String DEFAULT_AVATAR_URL = "/images/default-avatar.png";

    public MyPageResponseDto getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        MyPage myPage = myPageRepository.findByUserId(userId).orElse(null);
        
        return buildMyPageResponse(user, myPage);
    }

    public MyPageResponseDto getMyPageByNickname(String nickname, Long requestUserId) {
        // 먼저 MyPage의 nickname으로 조회 시도
        MyPage myPage = myPageRepository.findByNickname(nickname).orElse(null);
        
        User user;
        if (myPage != null) {
            // MyPage에서 찾았으면 해당 User 사용
            user = myPage.getUser();
        } else {
            // MyPage에 없으면 User의 nickname으로 조회
            user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            myPage = myPageRepository.findByUserId(user.getId()).orElse(null);
        }
        
        // 비공개 프로필이고 본인이 아닌 경우 접근 거부
        if (myPage != null && !myPage.getIsPublic() && 
            (requestUserId == null || !requestUserId.equals(user.getId()))) {
            throw new IllegalArgumentException("비공개 프로필입니다.");
        }
        
        return buildMyPageResponse(user, myPage);
    }

    private MyPageResponseDto buildMyPageResponse(User user, MyPage myPage) {
        Long userId = user.getId();

        List<Long> solvedProblems = submissionRepository.findSolvedProblemsByUserId(userId);

        List<SubmissionItem> recentSubmissions = submissionRepository
                .findRecentSubmissionsByUserId(userId, PageRequest.of(0, 5))
                .stream()
                .map(sub -> new SubmissionItem(
                        sub.getId(),
                        sub.getProblem().getProblemId(),
                        sub.getStatus().name(),
                        sub.getLanguage().getDisplayName(),
                        sub.getRuntimeMs() != null ? sub.getRuntimeMs() : 0,
                        sub.getSubmittedAt()
                ))
                .collect(Collectors.toList());

        Long totalSubmitted = submissionRepository.countByUserId(userId);
        Long totalSolved = submissionRepository.countSolvedByUserId(userId);
        double acceptanceRate = totalSubmitted > 0 ? (totalSolved * 100.0 / totalSubmitted) : 0.0;

        UserStats stats = new UserStats(
                totalSolved != null ? totalSolved.intValue() : 0,
                totalSubmitted != null ? totalSubmitted.intValue() : 0,
                Math.round(acceptanceRate * 10.0) / 10.0,
                0, 0, 0
        );

        List<String> preferredLanguageList = List.of();
        if (myPage != null && myPage.getPreferredLanguage() != null) {
            try {
                preferredLanguageList = objectMapper.readValue(
                        myPage.getPreferredLanguage(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
            } catch (JsonProcessingException e) {
                // JSON 파싱 실패 시 빈 리스트 유지
                preferredLanguageList = List.of();
            }
        }

        return MyPageResponseDto.builder()
                .id(myPage != null ? myPage.getId() : null)
                .userId(user.getId())
                .nickname(myPage != null && myPage.getNickname() != null 
                        ? myPage.getNickname() 
                        : user.getNickname())
                // 아바타 url이 따로 없을 경우 기본 이미지 url 적용
                .avatarUrl(myPage != null && myPage.getAvatarUrl() != null 
                        ? myPage.getAvatarUrl() 
                        : DEFAULT_AVATAR_URL)
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (myPageRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 마이페이지가 존재합니다.");
        }

        MyPage myPage = MyPage.builder()
                .user(user)
                .nickname(user.getNickname())
                .isPublic(true)
                .build();

        myPageRepository.save(myPage);
        return buildMyPageResponse(user, myPage);
    }

    @Transactional
    public MyPageResponseDto updateMyPage(Long userId, MyPageUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        MyPage myPage = myPageRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("마이페이지를 찾을 수 없습니다."));

        if (requestDto.getNickname() != null) {
            // User 테이블과 MyPage 테이블 모두에서 닉네임 중복 확인 (본인 제외)
            if (userRepository.existsByNicknameAndIdNot(requestDto.getNickname(), userId)) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
            if (myPageRepository.existsByNicknameAndUserIdNot(requestDto.getNickname(), userId)) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
            // MyPage와 User의 닉네임 모두 업데이트
            myPage.updateNickname(requestDto.getNickname());
            user.updateNickname(requestDto.getNickname());
        }
        if (requestDto.getBio() != null) {
            myPage.updateBio(requestDto.getBio());
        }
        if (requestDto.getPreferredLanguage() != null) {
            try {
                String jsonLanguage = objectMapper.writeValueAsString(requestDto.getPreferredLanguage());
                myPage.updatePreferredLanguage(jsonLanguage);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("선호 언어 JSON 변환 실패", e);
            }
        }
        if (requestDto.getIsPublic() != null) {
            myPage.updateIsPublic(requestDto.getIsPublic());
        }

        userRepository.save(user);  // User 엔티티 저장
        myPageRepository.save(myPage);
        return buildMyPageResponse(user, myPage);
    }

    @Transactional
    public void deleteMyPage(Long userId) {
        MyPage myPage = myPageRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("마이페이지를 찾을 수 없습니다."));

        myPageRepository.delete(myPage);
    }

    @Transactional
    public MyPageResponseDto initializeMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 기존 MyPage가 있으면 삭제하고 flush로 즉시 반영
        myPageRepository.findByUserId(userId).ifPresent(existingMyPage -> {
            myPageRepository.delete(existingMyPage);
            myPageRepository.flush(); // 즉시 DB에 반영
        });
        
        // 초기 상태로 MyPage 재생성 (회원가입 시와 동일)
        MyPage newMyPage = MyPage.builder()
                .user(user)
                .nickname(user.getNickname())
                .isPublic(true)
                .build();
        
        myPageRepository.save(newMyPage);
        return buildMyPageResponse(user, newMyPage);
    }
}
