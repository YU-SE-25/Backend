package com.unide.backend.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.mypage.dto.ReminderRequestDto;
import com.unide.backend.domain.mypage.dto.ReminderResponseDto;
import com.unide.backend.domain.mypage.service.ReminderService;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {
	private final ReminderService reminderService;

	/** 유저별 리마인더 전체 조회 */
	@GetMapping
	public ResponseEntity<List<ReminderResponseDto>> getReminders(@AuthenticationPrincipal PrincipalDetails principal) {
		User user = principal.getUser();
		return ResponseEntity.ok(reminderService.getRemindersByUser(user));
	}

	/** 리마인더 등록(처음 입력) */
	@PostMapping
	public ResponseEntity<ReminderResponseDto> addReminder(
			@AuthenticationPrincipal PrincipalDetails principal,
			@RequestBody ReminderRequestDto dto) {
		User user = principal.getUser();
		return ResponseEntity.ok(reminderService.saveReminder(user, dto));
	}

	/** 리마인더 수정(빼고 싶은 시간 빼고 입력) */
	@PatchMapping("/{reminderId}")
	public ResponseEntity<ReminderResponseDto> updateReminder(
			@PathVariable Long reminderId,
			@RequestBody ReminderRequestDto dto) {
		return ResponseEntity.ok(reminderService.updateReminder(reminderId, dto));
	}

	/** 리마인더 전체 삭제 */
	@DeleteMapping("/{reminderId}")
	public ResponseEntity<Void> deleteReminder(@PathVariable Long reminderId) {
		reminderService.deleteReminder(reminderId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/send-test")
	public ResponseEntity<Void> sendTestReminders() {
		reminderService.sendReminders();
		return ResponseEntity.ok().build();
	}
}
