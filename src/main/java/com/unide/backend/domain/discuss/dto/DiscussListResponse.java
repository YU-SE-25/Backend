package com.unide.backend.domain.discuss.dto;
import lombok.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussListResponse {
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<DiscussListItemDto> posts;
}
