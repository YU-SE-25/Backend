
package com.unide.backend.domain.qna.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddProblemRequest {

    private Long postId;
    private Long problemId;
}
