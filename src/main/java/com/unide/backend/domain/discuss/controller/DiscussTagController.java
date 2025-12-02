package com.unide.backend.domain.discuss.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.dto.TagDto;
import com.unide.backend.domain.discuss.service.DiscussTagService;
import com.unide.backend.global.dto.PageResponse;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dis_board")
public class DiscussTagController {

    private final DiscussTagService discussTagService;

    /**
     * 게시글 태그 설정 
     * POST /api/dis_board/{postId}/tag
     */
    @PostMapping("/{postId}/tag")
    public ResponseEntity<Void> updateTagsForPost(
            @PathVariable Long postId,
            @RequestBody TagUpdateRequest request
    ) {
        // Integer → Long 변환
        List<Long> tagIds = request.getTagIds()
                .stream()
                .map(Integer::longValue)
                .toList();

        discussTagService.addTagsToPost(postId, tagIds);

        return ResponseEntity.ok().build();
    }

    /**
     * 특정 게시글의 태그 조회
     * GET /api/dis_board/tag/list/{postId}
     */
    @GetMapping("/tag/list/{postId}")
    public ResponseEntity<List<Long>> getTagsByPost(@PathVariable Long postId) {

        List<Long> tagIds = discussTagService.getTagsByPost(postId);
        return ResponseEntity.ok(tagIds);
    }

    /**
     * 태그별 게시글 목록 조회 
     * GET /api/dis_board/tag/{tagId}/posts?page=1
     */
    @GetMapping("/tag/{tagId}/posts")
    public ResponseEntity<PageResponse<DiscussDto>> getPostsByTag(
            @PathVariable Long tagId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = userDetails != null ? userDetails.getUser().getId() : null;

        PageResponse<DiscussDto> response =
                discussTagService.getPostsByTag(tagId, page, viewerId);

        return ResponseEntity.ok(response);
    }

    /**
     * 전체 태그 목록 조회
     * GET /api/dis_board/tags
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getAllTags() {
        List<TagDto> tags = discussTagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    /**
     * 요청 바디 DTO (tagIds만 포함)
     */
    public static class TagUpdateRequest {
        private List<Integer> tagIds;

        public List<Integer> getTagIds() {
            return tagIds;
        }

        public void setTagIds(List<Integer> tagIds) {
            this.tagIds = tagIds;
        }
    }
}
