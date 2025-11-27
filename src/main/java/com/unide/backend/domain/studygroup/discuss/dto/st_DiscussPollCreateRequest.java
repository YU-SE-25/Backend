package com.unide.backend.domain.studygroup.discuss.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class st_DiscussPollCreateRequest {

    @JsonProperty("post_id")
    private Long post_id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("option1")
    private String option1;

    @JsonProperty("option2")
    private String option2;

    @JsonProperty("option3")
    private String option3;

    @JsonProperty("option4")
    private String option4;

    @JsonProperty("is_private")
    private Boolean is_private;

    @JsonProperty("allows_multi")
    private Boolean allows_multi;

    @JsonProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end_time;

    public List<String> extractOptions() {
        List<String> list = new ArrayList<>();
        if (option1 != null && !option1.isBlank()) list.add(option1);
        if (option2 != null && !option2.isBlank()) list.add(option2);
        if (option3 != null && !option3.isBlank()) list.add(option3);
        if (option4 != null && !option4.isBlank()) list.add(option4);
        return list;
    }
}
