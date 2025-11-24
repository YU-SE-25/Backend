package com.unide.backend.domain.discuss.controller;

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

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.service.DiscussService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dis_board")
public class DiscussController {
    private final DiscussService discussService;

    // 목록
    @GetMapping
    public List<DiscussDto> list(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum
    ) {
        return discussService.getDiscussList(pageNum);
    }
  
    @GetMapping("/list")
    public List<DiscussDto> listAll(
        @RequestParam(value = "page", defaultValue = "1") Integer pageNum){
    return discussService.getDiscussList(pageNum);}

    // 상세
    @GetMapping("/{postId}")
    public DiscussDto detail(@PathVariable("postId") Long postId) {
        return discussService.getDiscuss(postId);
    }

    // 작성
    @PostMapping
    public DiscussDto create(
        @AuthenticationPrincipal PrincipalDetails userDetails,
        @RequestBody DiscussDto discussDto) {

    Long authorId = userDetails.getUser().getId(); // 🔥 로그인 유저 ID 자동 추출

    return discussService.createDiscuss(discussDto, authorId);
}

    // 수정
    @PutMapping("/{postId}")
    public DiscussDto update(
            @PathVariable("postId") Long postId,
            @RequestBody DiscussDto discussDto
    ) {
        return discussService.updateDiscuss(postId, discussDto);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable("postId") Long postId) {
        discussService.deleteDiscuss(postId);
        
    }

    // 검색
    @GetMapping("/search")
    public List<DiscussDto> search(@RequestParam("keyword") String keyword) {
        return discussService.searchDiscusses(keyword);
    }
    @PostMapping("/{postId}/attach")
    public Map<String, Object> attachFile(
        @PathVariable Long postId,
        @RequestBody Map<String, String> request
) {
    String fileUrl = request.get("contents");   // 문서에 맞춰 contents 로 받음

    return discussService.attachFile(postId, fileUrl);
}


}
