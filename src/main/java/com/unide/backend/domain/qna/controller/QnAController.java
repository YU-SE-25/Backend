package com.unide.backend.domain.qna.controller;


import com.unide.backend.domain.qna.dto.QnADto;
import com.unide.backend.domain.qna.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public QnADto create(@RequestBody QnADto qnaDto) {
        return qnaService.createQnA(qnaDto);
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


}
