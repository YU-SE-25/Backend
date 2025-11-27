package com.unide.backend.domain.studygroup.discuss.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.studygroup.discuss.service.st_DiscussTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studygroup/{groupId}/discuss/tag")
public class st_DiscussTagController {

    private final st_DiscussTagService discussTagService;

    /**
     * 게시글에 태그 추가 (여러 개)
     * POST /api/studygroup/{groupId}/discuss/tag/add
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addTagsToPost(
            @PathVariable Long groupId,
            @RequestBody AddTagRequest request
    ) {

        // Integer → Long 변환
        List<Long> tagIdsAsLong = request.getTagIds()
                .stream()
                .map(Integer::longValue)
                .toList();

        discussTagService.addTagsToPost(groupId, request.getPostId(), tagIdsAsLong);

        return ResponseEntity.ok().build();
    }

    /**
     * 특정 게시글의 태그 조회
     * GET /api/studygroup/{groupId}/discuss/tag/list/{postId}
     */
    @GetMapping("/list/{postId}")
    public ResponseEntity<List<Long>> getTagsByPost(
            @PathVariable Long groupId,
            @PathVariable Long postId
    ) {

        List<Long> tagIds = discussTagService.getTagsByPost(groupId, postId);
        return ResponseEntity.ok(tagIds);
    }

    /**
     * 요청 바디 DTO
     */
    public static class AddTagRequest {
        private Long postId;
        private List<Integer> tagIds;  // 들어오는 값은 Integer일 가능성 → Long 변환해줌

        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }

        public List<Integer> getTagIds() { return tagIds; }
        public void setTagIds(List<Integer> tagIds) { this.tagIds = tagIds; }
    }
}
