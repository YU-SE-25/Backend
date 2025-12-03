package com.unide.backend.common.llm.service;

import com.unide.backend.common.llm.dto.OpenAiRequestDto;
import com.unide.backend.common.llm.dto.OpenAiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api-url}")
    private String apiUrl;

    public String getResponse(String prompt, String systemInstruction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<OpenAiRequestDto.Message> messages = List.of(
                OpenAiRequestDto.Message.builder().role("system").content(systemInstruction).build(),
                OpenAiRequestDto.Message.builder().role("user").content(prompt).build()
        );

        OpenAiRequestDto request = OpenAiRequestDto.builder()
                .model(model)
                .messages(messages)
                .temperature(0.7)
                .build();

        try {
            HttpEntity<OpenAiRequestDto> entity = new HttpEntity<>(request, headers);
            OpenAiResponseDto response = restTemplate.postForObject(apiUrl, entity, OpenAiResponseDto.class);

            if (response != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            log.error("LLM API 호출 중 오류 발생", e);
            throw new RuntimeException("AI 서비스 연결에 실패했습니다.");
        }
        
        return "응답을 받아오지 못했습니다.";
    }
}
