package com.unide.backend.domain.discuss.dto;

public class DiscussPollVoteRequest {

    // 사용자가 선택한 option_id 하나만 보내는 구조라면 이렇게
    private Long option_id;

    public Long getOption_id() {
        return option_id;
    }

    public void setOption_id(Long option_id) {
        this.option_id = option_id;
    }
}