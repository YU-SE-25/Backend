// 코드 IDE 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.submissions.controller;

import com.unide.backend.domain.submissions.dto.CodeRunRequestDto;
import com.unide.backend.domain.submissions.dto.CodeRunResponseDto;
import com.unide.backend.domain.submissions.service.DockerService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CodeIdeController {
    private final DockerService dockerService;

    /**
     * 작성한 코드를 Docker 환경에서 실행하는 API
     * @param principalDetails 현재 로그인한 사용자
     * @param requestDto 코드, 언어, 입력값
     * @return 실행 결과
     */
    @PostMapping("/run")
    public ResponseEntity<CodeRunResponseDto> runCode(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CodeRunRequestDto requestDto) {
        
        CodeRunResponseDto response = dockerService.runCode(requestDto);
        return ResponseEntity.ok(response);
    }
}
