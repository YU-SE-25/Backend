// 사용자 ID를 기반으로 DB에서 사용자 정보를 조회하는 서비스

package com.unide.backend.global.security.auth;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId = Long.parseLong(username);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        return new PrincipalDetails(user);
    }
}
