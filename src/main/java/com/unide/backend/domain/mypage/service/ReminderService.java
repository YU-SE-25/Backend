package com.unide.backend.domain.mypage.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;

import com.unide.backend.domain.mypage.entity.Reminder;
import com.unide.backend.domain.mypage.repository.ReminderRepository;
import com.unide.backend.domain.mypage.dto.ReminderRequestDto;
import com.unide.backend.domain.mypage.dto.ReminderResponseDto;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReminderService {
	private final ReminderRepository reminderRepository;
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	@Transactional(readOnly = true)
	public List<ReminderResponseDto> getRemindersByUser(User user) {
		return reminderRepository.findByUser(user).stream()
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

	@Transactional
	public ReminderResponseDto saveReminder(User user, ReminderRequestDto dto) {
		List<Reminder> duplicates = reminderRepository.findAllByUserAndDay(user, dto.getDay());
		if (!duplicates.isEmpty()) {
			Reminder toKeep = duplicates.get(0);
			for (int i = 1; i < duplicates.size(); i++) {
				reminderRepository.delete(duplicates.get(i));
			}

			java.util.List<String> newTimes = dto.getTimes();
			java.util.List<String> existingTimes = fromTimesJson(toKeep.getTimes());
			java.util.Set<String> mergedTimes = new java.util.HashSet<>(existingTimes);
			mergedTimes.addAll(newTimes);
			toKeep.updateTimes(toTimesJson(new java.util.ArrayList<>(mergedTimes)));
			Reminder saved = reminderRepository.save(toKeep);
			return toResponseDto(saved);
		} else {
			// insert
			Reminder reminder = Reminder.builder()
					.user(user)
					.day(dto.getDay())
					.times(toTimesJson(dto.getTimes()))
					.build();
			Reminder saved = reminderRepository.save(reminder);
			return toResponseDto(saved);
		}
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
	@Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
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
					try {
						MimeMessage mimeMessage = mailSender.createMimeMessage();
						MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

						Context context = new Context();
						context.setVariable("nickname", user.getNickname());
						context.setVariable("reminderTime", currentTime);
						context.setVariable("studyUrl", "http://localhost:3000/");

						String html = templateEngine.process("reminder-email.html", context);

						helper.setTo(user.getEmail()); // 받는 사람
						helper.setSubject("[Unide] 리마인더 알림"); // 제목
						helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

						mailSender.send(mimeMessage); // 최종 발송
					} catch (MessagingException e) {
						throw new RuntimeException("이메일 발송에 실패했습니다.", e);
					}
				}
			}
		}
	}

}
