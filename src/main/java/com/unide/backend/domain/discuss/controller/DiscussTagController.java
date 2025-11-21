package com.unide.backend.domain.discuss.controller;

import com.unide.backend.domain.discuss.service.DiscussTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dis_board/tag")
public class DiscussTagController {

    private final DiscussTagService discussTagService;

    /**
     * 게시글에 태그 추가 (여러 개)
     * POST /api/dis_board/tag/add
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addTagsToPost(@RequestBody AddTagRequest request) {

        // Integer → Long 변환 필요 (엔티티가 Long 타입이니까)
        List<Long> tagIdsAsLong = request.getTagIds()
                .stream()
                .map(Integer::longValue)
                .toList();

        discussTagService.addTagsToPost(request.getPostId(), tagIdsAsLong);

        return ResponseEntity.ok().build();
    }


    /**
     * 특정 게시글의 태그 조회
     * GET /api/dis_board/tag/list/{postId}
     */
    @GetMapping("/list/{postId}")
    public ResponseEntity<List<Long>> getTagsByPost(@PathVariable Long postId) {

        List<Long> tagIds = discussTagService.getTagsByPost(postId);

        return ResponseEntity.ok(tagIds);
    }


    /**
     * 요청 바디 DTO
     */
    public static class AddTagRequest {
        private Long postId;
        private List<Integer> tagIds;  // 요청은 Integer로 올 수도 있으므로 유지

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }

        public List<Integer> getTagIds() {
            return tagIds;
        }

        public void setTagIds(List<Integer> tagIds) {
            this.tagIds = tagIds;
        }
    }
}
