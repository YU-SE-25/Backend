package com.unide.backend.domain.mypage.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderResponseDto {
	private Long id;
	private int day;
	private List<String> times;
}
