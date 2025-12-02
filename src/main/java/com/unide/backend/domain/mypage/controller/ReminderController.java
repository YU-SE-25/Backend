package com.unide.backend.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

	/** 리마인더 등록 */
	@PostMapping
	public ResponseEntity<ReminderResponseDto> addReminder(
			@AuthenticationPrincipal PrincipalDetails principal,
			@RequestBody ReminderRequestDto dto) {
		User user = principal.getUser();
		return ResponseEntity.ok(reminderService.saveReminder(user, dto));
	}

	/** 리마인더 수정 */
	@PatchMapping("/{reminderId}")
	public ResponseEntity<ReminderResponseDto> updateReminder(
			@PathVariable Long reminderId,
			@RequestBody ReminderRequestDto dto) {
		return ResponseEntity.ok(reminderService.updateReminder(reminderId, dto));
	}

	/** 리마인더 삭제 */
	@DeleteMapping("/{reminderId}")
	public ResponseEntity<Void> deleteReminder(@PathVariable Long reminderId) {
		reminderService.deleteReminder(reminderId);
		return ResponseEntity.noContent().build();
	}
}
