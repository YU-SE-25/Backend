package com.unide.backend.domain.request.dto;

import com.unide.backend.domain.request.entity.RequestStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestListDto {

    private Long id;
    private String requesterName;
    private String title;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
