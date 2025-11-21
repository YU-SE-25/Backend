package com.unide.backend.domain.discuss.controller;

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.service.DiscussService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public DiscussDto create(@RequestBody DiscussDto discussDto) {
        return discussService.createDiscuss(discussDto);
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
}
