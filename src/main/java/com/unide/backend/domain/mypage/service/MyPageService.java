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
import com.unide.backend.domain.mypage.dto.UserGoalsResponseDto;
import com.unide.backend.domain.mypage.dto.UserStatsResponseDto;
import com.unide.backend.domain.mypage.entity.Goals;
import com.unide.backend.domain.mypage.entity.MyPage;
import com.unide.backend.domain.mypage.entity.Stats;
import com.unide.backend.domain.mypage.repository.GoalsRepository;
import com.unide.backend.domain.mypage.repository.MyPageRepository;
import com.unide.backend.domain.mypage.repository.StatsRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.domain.bookmark.repository.BookmarkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final UserRepository userRepository;
    private final SubmissionsRepository submissionsRepository;
    private final StatsRepository statsRepository;
    private final GoalsRepository goalsRepository;
    private final ObjectMapper objectMapper;
    private final StatsService statsService;
    private final GoalsService goalsService;
    private final BookmarkRepository bookmarkRepository;

    private static final String DEFAULT_AVATAR_URL = "/images/default-avatar.png";

    /** 1. userId로 마이페이지 조회 */
    public MyPageResponseDto getMyPage(Long userId) {
    User user = getUserById(userId);
    MyPage myPage = myPageRepository.findByUserId(userId).orElse(null);
    UserGoalsResponseDto goals = goalsService.getGoals(userId);
    UserStatsResponseDto stats = statsService.getStats(userId);

    return buildMyPageResponse(user, myPage, stats, goals);
    }


    /** 2. 닉네임으로 조회 */
    public MyPageResponseDto getMyPageByNickname(String nickname, Long requestUserId) {
        MyPage myPage = myPageRepository.findByNickname(nickname).orElse(null);

        User user = (myPage != null)
                ? myPage.getUser()
                : userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (myPage == null)
            myPage = myPageRepository.findByUserId(user.getId()).orElse(null);

        validateProfileAccess(myPage, user.getId(), requestUserId);

        return buildMyPageResponse(user, myPage);
    }

    /** 마이페이지 응답 DTO 조립 */
    private MyPageResponseDto buildMyPageResponse(
        User user,
        MyPage myPage,
        UserStatsResponseDto statsDto,
        UserGoalsResponseDto goalsDto
    ) {
        Long userId = user.getId();

        // 푼 문제, 북마크한 문제, 최근 제출 내역, 선호 언어 조회
        List<Long> solvedProblems = submissionsRepository.findSolvedProblemsByUserId(userId);
        List<SubmissionItem> recentSubmissions = getRecentSubmissions(userId);
        List<Long> bookmarkedProblems = bookmarkRepository.findBookmarkedProblemIdsByUserId(userId);
        List<String> preferredLanguageList = parsePreferredLanguages(myPage);

        return MyPageResponseDto.builder()
                .userId(user.getId())
                .nickname(getNickname(myPage, user))
                .avatarUrl(getAvatarUrl(myPage))
                .bio(getBio(myPage))
                .preferredLanguage(preferredLanguageList)
                .role(user.getRole().toString())
                .joinedAt(user.getCreatedAt())
                .updatedAt(myPage != null ? myPage.getUpdatedAt() : user.getUpdatedAt())
                .isPublic(getIsPublic(myPage))
                .isStudyAlarm(getIsStudyAlarm(myPage))
                .isDarkMode(getIsDarkMode(myPage))
                .solvedProblems(solvedProblems)
                .bookmarkedProblems(bookmarkedProblems)
                .recentSubmissions(recentSubmissions)
                .stats(statsDto)
                .goals(goalsDto)
                .build();
    }

    private MyPageResponseDto buildMyPageResponse(User user, MyPage myPage) {
        return buildMyPageResponse(user, myPage,
                statsService.getStats(user.getId()),
                goalsService.getGoals(user.getId()));
    }

    /* ====================== UPDATE LOGIC =========================== */

    @Transactional
    public UserStatsResponseDto updateUserStats(Long userId, UserStatsResponseDto statsDto) {
        User user = getUserById(userId);
        Stats stats = statsRepository.findByUserId(userId);

        if (stats == null) {
            stats = Stats.builder()
                    .user(user)
                    .totalSolved(statsDto.getTotalSolved())
                    .totalSubmitted(statsDto.getTotalSubmitted())
                    .acceptanceRate(statsDto.getAcceptanceRate())
                    .streakDays(statsDto.getStreakDays())
                    .ranking(statsDto.getRanking())
                    .rating(statsDto.getRating())
                    .build();
        } else {
            stats.updateTotalSolved(statsDto.getTotalSolved());
            stats.updateTotalSubmitted(statsDto.getTotalSubmitted());
            stats.updateAcceptanceRate(statsDto.getAcceptanceRate());
            stats.updateStreakDays(statsDto.getStreakDays());
            stats.updateRanking(statsDto.getRanking());
        }

        statsRepository.save(stats);
        return statsDto;
    }

    @Transactional
    public UserGoalsResponseDto updateUserGoals(Long userId, UserGoalsResponseDto goalsDto) {
        User user = getUserById(userId);
        Goals goals = goalsRepository.findByUserId(userId);

        String studyTimeJson = null;
        try {
            if (goalsDto.getStudyTimeByLanguage() != null)
                studyTimeJson = objectMapper.writeValueAsString(goalsDto.getStudyTimeByLanguage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("studyTimeByLanguage JSON 변환 실패", e);
        }

        if (goals == null) {
            goals = Goals.builder()
                    .user(user)
                    .dailyMinimumStudyMinutes(goalsDto.getDailyMinimumStudyMinutes())
                    .weeklyStudyGoalMinutes(goalsDto.getWeeklyStudyGoalMinutes())
                    .reminderTimes(String.join(",", goalsDto.getReminderTimes()))
                    .isReminderEnabled(goalsDto.getIsReminderEnabled())
                    .studyTimeByLanguage(studyTimeJson)
                    .build();
        } else {
            goals.updateDailyMinimumStudyMinutes(goalsDto.getDailyMinimumStudyMinutes());
            goals.updateWeeklyStudyGoalMinutes(goalsDto.getWeeklyStudyGoalMinutes());
            goals.updateReminderTimes(String.join(",", goalsDto.getReminderTimes()));
            goals.updateIsReminderEnabled(goalsDto.getIsReminderEnabled());
            goals.updateStudyTimeByLanguage(studyTimeJson);
        }

        goalsRepository.save(goals);
        return goalsDto;
    }

    /* ====================== UPDATE MYPAGE =========================== */

    @Transactional
    public MyPageResponseDto updateMyPage(Long userId, MyPageUpdateRequestDto requestDto) {
        User user = getUserById(userId);
        MyPage myPage = getMyPageByUserId(userId);

        updateNicknameIfPresent(requestDto.getNickname(), userId, user, myPage);
        updateBioIfPresent(requestDto.getBio(), myPage);
        updatePreferredLanguageIfPresent(requestDto.getPreferredLanguage(), myPage);
        updatePublicStatusIfPresent(requestDto.getIsPublic(), myPage);
        updateStudyAlarmIfPresent(requestDto.getIsStudyAlarm(), myPage);
        updateDarkModeIfPresent(requestDto.getIsDarkMode(), myPage);
        updateAvatarUrlIfPresent(requestDto.getAvatarUrl(), myPage);

        userRepository.save(user);
        myPageRepository.save(myPage);

        return buildMyPageResponse(user, myPage);
    }

    /* ====================== DELETE & INIT =========================== */

    @Transactional
    public void deleteMyPage(Long userId) {
        MyPage myPage = getMyPageByUserId(userId);
        myPageRepository.delete(myPage);
    }

    @Transactional
    public MyPageResponseDto initializeMyPage(Long userId) {
        User user = getUserById(userId);

        myPageRepository.findByUserId(userId).ifPresent(existing -> {
            myPageRepository.delete(existing);
            myPageRepository.flush();
        });

        MyPage newMyPage = createDefaultMyPage(user);
        myPageRepository.save(newMyPage);
        return buildMyPageResponse(user, newMyPage);
    }

    /* ====================== PRIVATE METHODS ====================== */

    private User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private MyPage getMyPageByUserId(Long userId) {
        return myPageRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("마이페이지를 찾을 수 없습니다."));
    }

    private void validateProfileAccess(MyPage myPage, Long ownerId, Long requestId) {
        if (myPage != null && !getIsPublic(myPage) &&
            (requestId == null || !requestId.equals(ownerId))) {
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

    private List<String> parsePreferredLanguages(MyPage myPage) {
        if (myPage == null || myPage.getPreferredLanguage() == null)
            return List.of();

        try {
            return objectMapper.readValue(
                    myPage.getPreferredLanguage(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
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

    private String getBio(MyPage myPage) {
        return (myPage != null && myPage.getBio() != null)
                ? myPage.getBio()
                : null;
    }

    private Boolean getIsPublic(MyPage myPage) {
        return (myPage != null) ? myPage.getIsPublic() : true;
    }

    private Boolean getIsStudyAlarm(MyPage myPage) {
        return (myPage != null) ? myPage.getIsStudyAlarm() : false;
    }

    private Boolean getIsDarkMode(MyPage myPage) {
        return (myPage != null) ? myPage.getIsDarkMode() : false;
    }
    private MyPage createDefaultMyPage(User user) {
        return MyPage.builder()
                .user(user)
                .nickname(user.getNickname())
                .isPublic(true)
                .isStudyAlarm(false)
                .isDarkMode(false)
                .build();
    }

    private void updateNicknameIfPresent(String nickname, Long userId, User user, MyPage myPage) {
        if (nickname == null) return;

        if (userRepository.existsByNicknameAndIdNot(nickname, userId) ||
            myPageRepository.existsByNicknameAndUserIdNot(nickname, userId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        myPage.updateNickname(nickname);
        user.updateNickname(nickname);
    }

    private void updateBioIfPresent(String bio, MyPage myPage) {
        if (bio != null) myPage.updateBio(bio);
    }

    private void updatePreferredLanguageIfPresent(List<String> preferredLanguage, MyPage myPage) {
        if (preferredLanguage == null) return;

        try {
            String json = objectMapper.writeValueAsString(preferredLanguage);
            myPage.updatePreferredLanguage(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("선호 언어 JSON 변환 실패", e);
        }
    }

    private void updatePublicStatusIfPresent(Boolean isPublic, MyPage myPage) {
        if (isPublic != null) myPage.updateIsPublic(isPublic);
    }

    private void updateStudyAlarmIfPresent(Boolean isStudyAlarm, MyPage myPage) {
        if (isStudyAlarm != null) myPage.updateIsStudyAlarm(isStudyAlarm);
    }

    private void updateDarkModeIfPresent(Boolean isDarkMode, MyPage myPage) {
        if (isDarkMode != null) myPage.updateIsDarkMode(isDarkMode);
    }

    private void updateAvatarUrlIfPresent(String avatarUrl, MyPage myPage) {
        if (avatarUrl != null) myPage.updateAvatarUrl(avatarUrl);
    }
}
