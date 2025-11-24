package com.unide.backend.domain.qna.controller;


import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.qna.dto.QnADto;
import com.unide.backend.domain.qna.service.QnAService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna_board")



public class QnAController {
     private final QnAService qnaService;

    // 목록
    @GetMapping
    public List<QnADto> list(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum
    ) {
        return qnaService.getQnAList(pageNum);
    }
    @GetMapping("/list")
    public List<QnADto> listAll(
        @RequestParam(value = "page", defaultValue = "1") Integer pageNum){ 
    return qnaService.getQnAList(pageNum);}

    // 상세
    @GetMapping("/{postId}")
    public QnADto detail(@PathVariable("postId") Long postId) {
        return qnaService.getQnA(postId);
    }

    // 작성
     @PostMapping
    public QnADto create(
        @AuthenticationPrincipal PrincipalDetails userDetails,
        @RequestBody QnADto qnaDto) {

    Long authorId = userDetails.getUser().getId(); // 🔥 로그인 유저 ID 자동 추출

    return qnaService.createQnA(qnaDto, authorId);
}

    // 수정
    @PutMapping("/{postId}")
    public QnADto update(
            @PathVariable("postId") Long postId,
            @RequestBody QnADto qnaDto
    ) {
        return qnaService.updateQnA(postId, qnaDto);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable("postId") Long postId) {
        qnaService.deleteQnA(postId);
    }


    // 검색
    @GetMapping("/search")
    public List<QnADto> search(@RequestParam("keyword") String keyword) {
        return qnaService.searchQnAs(keyword);
    }
    //첨부파일 첨가
    @PostMapping("/{postId}/attach")
    public Map<String, Object> attachFile(
        @PathVariable Long postId,
        @RequestBody Map<String, String> request
) {
    String fileUrl = request.get("contents");   // 문서에 맞춰 contents 로 받음

    return qnaService.attachFile(postId, fileUrl);
}



}
