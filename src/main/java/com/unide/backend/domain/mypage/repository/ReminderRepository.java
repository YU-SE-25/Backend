package com.unide.backend.domain.mypage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.mypage.entity.Reminder;
import com.unide.backend.domain.user.entity.User;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
	List<Reminder> findByUser(User user);
	
	Optional<Reminder> findByUserAndDay(User user, int day);
	
	List<Reminder> findAllByUserAndDay(User user, int day);
	
	// 특정 유저의 모든 리마인더 가져오기
    List<Reminder> findByUserId(Long userId);

    // 특정 유저의 모든 리마인더 삭제
    void deleteByUserId(Long userId);

}
