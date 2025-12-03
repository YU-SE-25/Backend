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
}
