// 인증 관련 비즈니스 로직을 처리하는 서비스 클래스

package com.unide.backend.domain.auth.service;

import com.unide.backend.domain.auth.dto.AvailabilityResponseDto;
import com.unide.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;

    /**
     * 이메일 사용 가능 여부를 확인하는 메서드
     * @param email 검사할 이메일
     * @return 사용 가능 여부와 메시지를 담은 DTO
     */
    public AvailabilityResponseDto checkEmailAvailability(String email) {
        // userRepository에 email이 존재하는지 확인(중복 확인). 존재하면 isAvailable은 false, 존재하지 않으면 true
        boolean isAvailable = !userRepository.existsByEmail(email);
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return new AvailabilityResponseDto(isAvailable, message);
    }

    /**
     * 닉네임 사용 가능 여부를 확인하는 메서드
     * @param nickname 검사할 닉네임
     * @return 사용 가능 여부와 메시지를 담은 DTO
     */
    public AvailabilityResponseDto checkNicknameAvailability(String nickname) {
        // userRepository에 nickname이 존재하는지 확인(중복 확인). 존재하면 isAvailable은 false, 존재하지 않으면 true
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        String message = isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return new AvailabilityResponseDto(isAvailable, message);
    }
}
