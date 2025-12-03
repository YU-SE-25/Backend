package com.unide.backend.common.llm.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class OpenAiRequestDto {
    private String model;
    private List<Message> messages;
    private double temperature;

    @Getter
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}
