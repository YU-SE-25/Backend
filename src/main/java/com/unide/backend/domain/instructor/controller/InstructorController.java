// 강사 지원 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.instructor.controller;

import com.unide.backend.domain.instructor.dto.InstructorApplicationRequestDto;
import com.unide.backend.domain.instructor.dto.InstructorApplicationResponseDto;
import com.unide.backend.domain.instructor.service.InstructorService;
import com.unide.backend.global.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorController {
    private final InstructorService instructorService;

    @PostMapping("/application/portfolio")
    public ResponseEntity<InstructorApplicationResponseDto> submitApplication(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody InstructorApplicationRequestDto requestDto) {
        InstructorApplicationResponseDto response = instructorService.submitApplication(principalDetails.getUser(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
