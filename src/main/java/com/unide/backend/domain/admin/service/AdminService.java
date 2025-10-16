// 관리자 기능 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.admin.service;

import com.unide.backend.domain.admin.dto.RoleChangeRequestDto;
import com.unide.backend.domain.admin.dto.RoleChangeResponseDto;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;

    @Transactional
    public RoleChangeResponseDto changeUserRole(Long userId, RoleChangeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 사용자를 찾을 수 없습니다: " + userId));

        UserRole oldRole = user.getRole();
        UserRole newRole = requestDto.getNewRole();

        user.changeRole(newRole);

        return RoleChangeResponseDto.builder()
                .userId(user.getId())
                .oldRole(oldRole)
                .newRole(newRole)
                .message("사용자 역할이 성공적으로 변경되었습니다.")
                .build();
    }
}
