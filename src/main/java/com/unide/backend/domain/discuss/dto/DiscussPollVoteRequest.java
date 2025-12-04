package com.unide.backend.domain.discuss.dto;

public class DiscussPollVoteRequest {

    // 사용자가 선택한 '보기 번호(label)' 하나만 보내는 구조
    private Integer label;   // 1, 2, 3, ...

    public Integer getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }
}
