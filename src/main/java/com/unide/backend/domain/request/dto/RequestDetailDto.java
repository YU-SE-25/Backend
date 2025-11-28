package com.unide.backend.domain.request.dto;

import com.unide.backend.domain.request.entity.RequestStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDetailDto {

    private Long id;
    private Long requesterId;
    private String requesterName;

    private String title;
    private String content;

    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}

