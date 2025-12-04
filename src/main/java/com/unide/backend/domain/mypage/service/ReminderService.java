package com.unide.backend.domain.mypage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mypage.entity.Reminder;
import com.unide.backend.domain.mypage.repository.ReminderRepository;
import com.unide.backend.domain.mypage.dto.ReminderRequestDto;
import com.unide.backend.domain.mypage.dto.ReminderResponseDto;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReminderService {
	private final com.unide.backend.common.mail.MailService mailService;

	private final ReminderRepository reminderRepository;

	@Transactional(readOnly = true)
	public List<ReminderResponseDto> getRemindersByUser(User user) {
		return reminderRepository.findByUser(user).stream()
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

	@Transactional
	public ReminderResponseDto saveReminder(User user, ReminderRequestDto dto) {
		Reminder reminder = Reminder.builder()
				.user(user)
				.day(dto.getDay())
				.times(toTimesJson(dto.getTimes()))
				.build();
		Reminder saved = reminderRepository.save(reminder);
		return toResponseDto(saved);
	}

	@Transactional
	public ReminderResponseDto updateReminder(Long id, ReminderRequestDto dto) {
		Reminder reminder = reminderRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("알림 정보 없음"));
		reminder.updateDay(dto.getDay());
		reminder.updateTimes(toTimesJson(dto.getTimes()));
		Reminder saved = reminderRepository.save(reminder);
		return toResponseDto(saved);
	}

	@Transactional
	public void deleteReminder(Long id) {
		reminderRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public ReminderResponseDto getReminder(Long id) {
		Reminder reminder = reminderRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("알림 정보 없음"));
		return toResponseDto(reminder);
	}

	// 엔티티 → DTO 변환
	private ReminderResponseDto toResponseDto(Reminder reminder) {
		return ReminderResponseDto.builder()
				.id(reminder.getId())
				.day(reminder.getDay())
				.times(fromTimesJson(reminder.getTimes()))
				.build();
	}

	// List<String> → JSON 문자열 변환
	private String toTimesJson(List<String> times) {
		try {
			return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(times);
		} catch (Exception e) {
			return "[]";
		}
	}

	// JSON 문자열 → List<String> 변환
	private List<String> fromTimesJson(String timesJson) {
		try {
			return new com.fasterxml.jackson.databind.ObjectMapper().readValue(
					timesJson,
					new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}
			);
		} catch (Exception e) {
			return List.of();
		}
	}

	// 매일 0시마다 모든 리마인더를 확인하여 해당 요일/시간에 메일 발송
	@org.springframework.scheduling.annotation.Scheduled(cron = "0 * * * * *") // 매 분마다 테스트용, 실제는 "0 0 * * * *" 등으로 변경
	@Transactional(readOnly = true)
	public void sendReminders() {
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		int todayNum = now.getDayOfWeek().getValue(); // 월=1, 일=7
		String currentTime = String.format("%02d:%02d", now.getHour(), now.getMinute());
		List<Reminder> reminders = reminderRepository.findAll();
		for (Reminder reminder : reminders) {
			// day는 int, times는 String (JSON)
			if (reminder.getTimes() == null || reminder.getTimes().isBlank()) continue;
			if (reminder.getDay() != todayNum) continue;
			List<String> times = fromTimesJson(reminder.getTimes());
			if (times.contains(currentTime)) {
				User user = reminder.getUser();
				if (user != null && user.getEmail() != null) {
					String subject = "리마인더 알림";
					String body = String.format("안녕하세요, %s님!\n설정하신 리마인더 시간(%s)에 알림을 드립니다.", user.getNickname(), currentTime);
					mailService.sendEmail(user.getEmail(), subject, body);
				}
			}
		}
	}
}
