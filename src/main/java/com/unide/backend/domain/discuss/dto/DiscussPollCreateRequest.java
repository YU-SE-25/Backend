package com.unide.backend.domain.discuss.dto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiscussPollCreateRequest {

    private Long post_id;
    private String title;

    private String option1;
    private String option2;
    private String option3;
    private String option4;

    private Boolean is_private;
    private Boolean allows_multi;

    private LocalDateTime end_time;

    public Long getPost_id() { return post_id; }
    public String getTitle() { return title; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public Boolean getIs_private() { return is_private; }
    public Boolean getAllows_multi() { return allows_multi; }
    public LocalDateTime getEnd_time() { return end_time; }

    // null 아닌 옵션만 추출
    public List<String> extractOptions() {
        List<String> list = new ArrayList<>();
        if (option1 != null && !option1.isBlank()) list.add(option1);
        if (option2 != null && !option2.isBlank()) list.add(option2);
        if (option3 != null && !option3.isBlank()) list.add(option3);
        if (option4 != null && !option4.isBlank()) list.add(option4);
        return list;
    }
    public void setPost_id(Long post_id) {
    this.post_id = post_id;
}
}
