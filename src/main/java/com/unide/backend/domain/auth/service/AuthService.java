// 인증 관련 비즈니스 로직을 처리하는 서비스 클래스

package com.unide.backend.domain.auth.service;

import com.unide.backend.domain.auth.dto.AvailabilityResponseDto;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.unide.backend.domain.auth.dto.EmailRequestDto;

import com.unide.backend.domain.terms.entity.UserTermsConsent;
import com.unide.backend.domain.auth.dto.BlacklistCheckRequestDto;
import com.unide.backend.domain.auth.dto.BlacklistCheckResponseDto;
import com.unide.backend.domain.admin.repository.BlacklistRepository;
import com.unide.backend.domain.auth.dto.RegisterRequestDto;
import com.unide.backend.domain.terms.repository.UserTermsConsentRepository;
import com.unide.backend.domain.auth.entity.EmailVerificationCode;
import com.unide.backend.domain.auth.repository.EmailVerificationCodeRepository;

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
        // 1. 이메일로 사용자를 찾음
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 고유한 인증 토큰 생성
        String token = UUID.randomUUID().toString();

        // 3. 생성된 토큰을 DB에 저장 (유효시간: 10분)
        EmailVerificationCode verificationCode = EmailVerificationCode.builder()
                .user(user)
                .verificationToken(token)
                .purpose(EmailVerificationCode.VerificationPurpose.SIGNUP)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        emailVerificationCodeRepository.save(verificationCode);

        // 4. 이메일을 구성 및 발송
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(user.getEmail());
        // message.setSubject("[Unide] 회원가입 이메일 인증");
        // message.setText("Unide 회원가입을 완료하려면 다음 링크를 클릭하세요: \n"
        //         + "http://localhost:3000/verify-email?token=" + token); // 링크는 실제 프론트엔드 주소로 변경해야 함
        // mailSender.send(message);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // 프론트엔드에서 사용할 인증 페이지 URL
            String url = "http://localhost:3000/verify-email?token=" + token;

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

}
