package com.unide.backend.domain.mypage.service;

import java.util.List;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unide.backend.domain.bookmark.repository.BookmarkRepository;
import com.unide.backend.domain.mypage.dto.MyPageResponseDto;
import com.unide.backend.domain.mypage.dto.MyPageUpdateRequestDto;
import com.unide.backend.domain.mypage.dto.ReminderRequestDto;
import com.unide.backend.domain.mypage.dto.ReminderResponseDto;
import com.unide.backend.domain.mypage.dto.SubmissionItem;
import com.unide.backend.domain.mypage.dto.UserGoalsRequestDto;
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
    private final ReminderService reminderService;

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

        // 리마인더 리스트 조회
        List<ReminderResponseDto> reminders = reminderService.getRemindersByUser(user);

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
                .reminders(reminders)
                .build();
    }
    // 리마인더 등록
    @Transactional
    public ReminderResponseDto addReminder(Long userId, ReminderRequestDto dto) {
        User user = getUserById(userId);
        return reminderService.saveReminder(user, dto);
    }

    // 리마인더 수정
    @Transactional
    public ReminderResponseDto updateReminder(Long reminderId, ReminderRequestDto dto) {
        return reminderService.updateReminder(reminderId, dto);
    }

    // 리마인더 삭제
    @Transactional
    public void deleteReminder(Long reminderId) {
        reminderService.deleteReminder(reminderId);
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
    public void updateUserGoals(Long userId, UserGoalsRequestDto requestDto) {
        User user = getUserById(userId);
        Goals goals = goalsRepository.findByUserId(userId);

        String studyTimeJson = null;
        try {
            if (requestDto.getStudyTimeByLanguage() != null)
                studyTimeJson = objectMapper.writeValueAsString(requestDto.getStudyTimeByLanguage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("studyTimeByLanguage JSON 변환 실패", e);
        }

        if (goals == null) {
            goals = Goals.builder()
                    .user(user)
                    .dailyMinimumStudyMinutes(requestDto.getDailyMinimumStudyMinutes())
                    .weeklyStudyGoalMinutes(requestDto.getWeeklyStudyGoalMinutes())
                    .studyTimeByLanguage(studyTimeJson)
                    .build();
        } else {
            goals.updateDailyMinimumStudyMinutes(requestDto.getDailyMinimumStudyMinutes());
            goals.updateWeeklyStudyGoalMinutes(requestDto.getWeeklyStudyGoalMinutes());
            goals.updateStudyTimeByLanguage(studyTimeJson);
        }

        goalsRepository.save(goals);
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
        updateAvatarFileIfPresent(requestDto.getAvatarImageFile(), myPage);

        // goals 수정
        if (requestDto.getGoals() != null) {
            updateUserGoals(userId, requestDto.getGoals());
        }
        // reminders 수정 (기존 삭제 후 새로 등록, 단순화 예시)
        if (requestDto.getReminders() != null) {
            List<ReminderResponseDto> existingReminders = reminderService.getRemindersByUser(user);
            existingReminders.forEach(r -> deleteReminder(r.getId()));
            requestDto.getReminders().forEach(reminderDto -> addReminder(userId, reminderDto));
        }

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

        // 마이페이지 삭제
        myPageRepository.findByUserId(userId).ifPresent(existing -> {
            myPageRepository.delete(existing);
            myPageRepository.flush();
        });

        // 목표(Goals) 삭제
        Goals goals = goalsRepository.findByUserId(userId);
        if (goals != null) {
            goalsRepository.delete(goals);
        }

        // 리마인더 전체 삭제
        List<ReminderResponseDto> reminders = reminderService.getRemindersByUser(user);
        reminders.forEach(r -> deleteReminder(r.getId()));

        // 마이페이지 기본값 생성
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

    private void updateAvatarFileIfPresent(MultipartFile avatarFile, MyPage myPage) {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarUrl = saveAvatarFile(avatarFile);
            myPage.updateAvatarUrl(avatarUrl);
        }
        // avatarFile이 null 또는 비어있으면 기존 이미지 유지
    }

    private String saveAvatarFile(MultipartFile file) {
        String uploadDir = "/uploads/avatars/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("아바타 파일 저장 실패", e);
        }
        return dest.getPath(); // 또는 URL로 변환
    }
}