// 파일 업로드 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.common.file.controller;

import com.unide.backend.common.file.dto.FileUploadResponseDto;
import com.unide.backend.common.file.service.FileService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {

    private final FileService fileService;

    @PostMapping("/portfolio")
    public ResponseEntity<FileUploadResponseDto> uploadPortfolio(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam("file") MultipartFile file) throws IOException {

        FileUploadResponseDto response = fileService.uploadPortfolio(principalDetails.getUser(), file);
        return ResponseEntity.ok(response);
    }
}
