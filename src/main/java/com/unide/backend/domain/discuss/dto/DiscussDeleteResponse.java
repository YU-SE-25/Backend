package com.unide.backend.domain.discuss.dto;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussDeleteResponse {
    private String message;
    private Long deletedId;
    private String redirect;      // "/api/dis_board_list/1"
}
