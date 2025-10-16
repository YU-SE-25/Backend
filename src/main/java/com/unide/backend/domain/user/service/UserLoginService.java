// 사용자의 로그인 관련 상태 변경을 전담하는 서비스

package com.unide.backend.domain.user.service;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserLoginService {
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processLoginFailure(User user, int maxFailures, Duration lockoutDuration) {
        user.onLoginFailure(maxFailures, lockoutDuration);
        userRepository.save(user);
    }
}
