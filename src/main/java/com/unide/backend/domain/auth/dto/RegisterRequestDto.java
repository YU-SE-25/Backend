// 회원가입 시 클라이언트의 요청을 담는 DTO

package com.unide.backend.domain.auth.dto;

import com.unide.backend.domain.terms.entity.Terms;
import com.unide.backend.domain.user.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다. (010-XXXX-XXXX)")
    private String phone;

    @NotNull(message = "역할은 필수 선택 값입니다.")
    private UserRole role;

    @NotEmpty(message = "필수 약관에 모두 동의해야 합니다.")
    private List<Terms> agreedTerms;

    private String portfolioFileUrl;

    private String originalFileName;
    
    private Long fileSize;
    
    private List<String> portfolioLinks;
}
