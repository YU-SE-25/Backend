// 인증 관련 비즈니스 로직을 처리하는 서비스 클래스

package com.unide.backend.domain.auth.service;

import com.unide.backend.domain.auth.dto.AvailabilityResponseDto;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.domain.auth.dto.EmailRequestDto;
import com.unide.backend.domain.terms.entity.UserTermsConsent;
import com.unide.backend.domain.auth.dto.BlacklistCheckRequestDto;
import com.unide.backend.domain.auth.dto.BlacklistCheckResponseDto;
import com.unide.backend.domain.admin.repository.BlacklistRepository;
import com.unide.backend.domain.auth.dto.RegisterRequestDto;
import com.unide.backend.domain.terms.repository.UserTermsConsentRepository;
import com.unide.backend.domain.auth.entity.EmailVerificationCode;
import com.unide.backend.domain.auth.repository.EmailVerificationCodeRepository;
import com.unide.backend.domain.user.entity.UserStatus;
import com.unide.backend.domain.auth.dto.WelcomeEmailRequestDto;
import com.unide.backend.domain.auth.dto.LoginRequestDto;
import com.unide.backend.domain.auth.dto.LoginResponseDto;
import com.unide.backend.domain.user.entity.UserStatus;
import com.unide.backend.global.exception.AuthException;
import com.unide.backend.global.security.jwt.JwtTokenProvider;
import com.unide.backend.domain.auth.entity.RefreshToken;
import com.unide.backend.domain.auth.repository.RefreshTokenRepository;
import com.unide.backend.domain.auth.dto.TokenRefreshRequestDto;
import com.unide.backend.domain.auth.entity.RefreshToken;
import com.unide.backend.domain.auth.repository.RefreshTokenRepository;
import com.unide.backend.domain.auth.dto.LogoutRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final BlacklistRepository blacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserTermsConsentRepository userTermsConsentRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private static final int MAX_LOGIN_FAILURES = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(10);
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

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

    /**
     * 휴대폰 번호 사용 가능 여부를 확인하는 메서드
     * @param phone 검사할 휴대폰 번호
     * @return 사용 가능 여부와 메시지를 담은 DTO
    */
    public AvailabilityResponseDto checkPhoneAvailability(String phone) {
        // userRepository에 phone이 존재하는지 확인(중복 확인). 존재하면 isAvailable은 false, 존재하지 않으면 true
        boolean isAvailable = !userRepository.existsByPhone(phone);
        String message = isAvailable ? "사용 가능한 휴대폰 번호입니다." : "이미 등록된 휴대폰 번호입니다.";
        return new AvailabilityResponseDto(isAvailable, message);
    }

    /**
     * 사용자가 블랙리스트에 등록되어 있는지 확인하는 메서드
     * @param requestDto 이름, 이메일, 휴대폰 번호가 담긴 DTO
     * @return 블랙리스트 여부와 메시지를 담은 DTO
    */
    public BlacklistCheckResponseDto checkBlacklistStatus(BlacklistCheckRequestDto requestDto) {
        // 이메일 또는 휴대폰 번호가 블랙리스트에 등록되어 있는지 확인
        boolean isBlacklisted = blacklistRepository.existsByEmailOrPhone(requestDto.getEmail(), requestDto.getPhone());
        String message = isBlacklisted ? "회원가입이 제한된 사용자입니다." : "블랙리스트 대상이 아닙니다.";
        return new BlacklistCheckResponseDto(isBlacklisted, message);
    }

    /**
     * 최종 회원가입을 처리하는 메서드
     * @param requestDto 회원가입 요청 DTO
     * @return 새로 생성된 사용자 ID
    */
    @Transactional
    public Long registerUser(RegisterRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User newUser = User.builder()
                .email(requestDto.getEmail())
                .passwordHash(encodedPassword)
                .name(requestDto.getName())
                .nickname(requestDto.getNickname())
                .phone(requestDto.getPhone())
                .role(requestDto.getRole())
                .build();

        User savedUser = userRepository.save(newUser);
        
        if (requestDto.getAgreedTerms() != null) {
            requestDto.getAgreedTerms().forEach(termsCode -> {
                UserTermsConsent consent = UserTermsConsent.builder()
                        .user(savedUser)
                        .termsCode(termsCode)
                        .version("1.0")
                        .agreed(true)
                        .build();
                
                savedUser.addUserTermsConsent(consent);
                userTermsConsentRepository.save(consent); 
            });
        }

        return savedUser.getId();
    }

    /**
     * 회원가입 인증 이메일을 발송하는 메서드
     * @param requestDto 이메일 주소를 담은 DTO
    */
    @Transactional
    public void sendVerificationEmail(EmailRequestDto requestDto) {
        // 이메일로 사용자를 찾음
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 고유한 인증 토큰 생성
        String token = UUID.randomUUID().toString();

        // 생성된 토큰을 DB에 저장 (유효시간: 10분)
        EmailVerificationCode verificationCode = EmailVerificationCode.builder()
                .user(user)
                .verificationToken(token)
                .purpose(EmailVerificationCode.VerificationPurpose.SIGNUP)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        emailVerificationCodeRepository.save(verificationCode);

        // 이메일 구성 및 발송
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // 프론트엔드에서 사용할 인증 페이지 URL
            String url = "http://localhost:8080/api/auth/email/verify-link?token=" + token;

            Context context = new Context();
            context.setVariable("verificationUrl", url);

            String html = templateEngine.process("verify-email", context);

            helper.setTo(user.getEmail()); // 받는 사람
            helper.setSubject("[Unide] 회원가입 이메일 인증"); // 제목
            helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

            mailSender.send(mimeMessage); // 최종 발송
            
        } catch (MessagingException e) {
            // 이메일 발송 중 오류가 발생하면 예외를 던져 트랜잭션을 롤백함
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 이메일 인증 토큰을 검증하고 계정을 활성화하는 메서드
     * @param token 이메일로 발송된 인증 토큰
    */
    @Transactional
    public void verifyEmail(String token) {
        // 토큰으로 인증 정보를 찾음
        EmailVerificationCode verificationCode = emailVerificationCodeRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다."));

        // 토큰이 만료되었는지 확인
        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증 토큰이 만료되었습니다.");
        }
        
        // 이미 사용된 토큰인지 확인
        if (verificationCode.getUsedAt() != null) {
            throw new IllegalArgumentException("이미 사용된 인증 토큰입니다.");
        }

        // 토큰 사용 처리
        verificationCode.useToken();

        // 사용자 계정 활성화
        User user = verificationCode.getUser();
        user.activateAccount();
    }

    /**
     * 회원가입 환영 이메일을 발송하는 메서드
     * @param requestDto 사용자 ID와 이메일을 담은 DTO
    */
    public void sendWelcomeEmail(WelcomeEmailRequestDto requestDto) {
        // ID로 사용자를 찾음
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // HTML 이메일을 구성 및 발송
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // 프론트엔드의 로그인 페이지 URL
            String loginUrl = "http://localhost:3000/login"; 

            Context context = new Context();
            context.setVariable("nickname", user.getNickname());
            context.setVariable("loginUrl", loginUrl);

            // 템플릿을 사용해 HTML을 생성
            String html = templateEngine.process("welcome-email", context);

            helper.setTo(user.getEmail());
            helper.setSubject("[Unide] 가입을 환영합니다!");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("환영 이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 로그인 처리 메서드 (POST /api/auth/login)
     * @param requestDto 로그인 요청 DTO (이메일, 비밀번호)
     * @return 로그인 응답 DTO (토큰, 사용자 정보)
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 이메일로 사용자 조회
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new AuthException("존재하지 않는 사용자입니다."));

        // 계정 상태 확인 (PENDING이면 로그인 거부)
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthException("계정이 활성화되지 않았거나 정지된 상태입니다. (상태: " + user.getStatus() + ")");
        }

        // 계정 잠금 상태 확인
        if (user.isLocked()) {
            throw new AuthException(String.format("계정이 잠금되었습니다. %s 이후에 다시 시도해 주세요.", user.getLockoutUntil()));
        }
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPasswordHash())) {
            user.onLoginFailure(MAX_LOGIN_FAILURES, LOCKOUT_DURATION);
            userRepository.save(user); 

            if (user.isLocked()) {
                 throw new AuthException("비밀번호가 일치하지 않습니다. 로그인 실패 횟수 초과로 계정이 잠금되었습니다.");
            }

            throw new AuthException("비밀번호가 일치하지 않습니다.");
        }
        
        // 로그인 성공 처리
        user.onLoginSuccess();
        userRepository.save(user);
        
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        Long expiresIn = 3600L; // 토큰 만료 시간

        // keepLogin이 true일 때만(로그인 유지 기능 활성화 시) refresh token 발급
        String refreshToken = null;

        if (requestDto.isKeepLogin()) {
            final String newTokenValue = jwtTokenProvider.createRefreshToken(user);
            final LocalDateTime tokenExpires = LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshExpirationMs() / 1000);

            refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                    token -> token.updateToken(newTokenValue, tokenExpires),
                    () -> {
                        RefreshToken newRefreshToken = RefreshToken.builder()
                            .user(user)
                            .tokenValue(newTokenValue)
                            .expiresAt(tokenExpires)
                            .build();
                        refreshTokenRepository.save(newRefreshToken);
                    }
                );
            
            refreshToken = newTokenValue;
        }

        // 응답 DTO 구성
        LoginResponseDto.UserInfo userInfo = LoginResponseDto.UserInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .user(userInfo)
                .build();
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 메서드
     * @param requestDto 리프레시 토큰을 담은 DTO
     * @return 새로 발급된 액세스 토큰
    */
    @Transactional
    public String refreshToken(TokenRefreshRequestDto requestDto) {
        String refreshTokenValue = requestDto.getRefreshToken();

        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // DB에서 리프레시 토큰 조회
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰을 찾을 수 없습니다."));

        // 토큰 만료 시간 확인
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken); // 만료된 토큰 삭제
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        // 새로운 액세스 토큰 생성
        User user = refreshToken.getUser();
        return jwtTokenProvider.createAccessToken(user);
    }

    /**
     * 로그아웃을 처리하는 메서드 (DB에서 리프레시 토큰 삭제)
     * @param requestDto 리프레시 토큰을 담은 DTO
    */
    @Transactional
    public void logout(LogoutRequestDto requestDto) {
        String refreshTokenValue = requestDto.getRefreshToken();
        
        // DB에서 해당 리프레시 토큰을 찾아 삭제
        refreshTokenRepository.findByTokenValue(refreshTokenValue)
                .ifPresent(refreshTokenRepository::delete);
    }
}
