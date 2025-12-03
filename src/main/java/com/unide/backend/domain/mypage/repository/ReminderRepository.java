package com.unide.backend.domain.mypage.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.unide.backend.domain.mypage.entity.Reminder;
import com.unide.backend.domain.user.entity.User;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
	List<Reminder> findByUser(User user);
}
