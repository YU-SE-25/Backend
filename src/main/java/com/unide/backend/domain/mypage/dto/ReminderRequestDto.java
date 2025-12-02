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
public class ReminderRequestDto {
    private int day; // 1~7 (1=월요일, 7=일요일)
    private List<String> times; // ["08:00", "21:00"] 등
}