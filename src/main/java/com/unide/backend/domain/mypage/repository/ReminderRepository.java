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
}
