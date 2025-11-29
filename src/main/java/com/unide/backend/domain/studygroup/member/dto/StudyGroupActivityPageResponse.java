package com.unide.backend.domain.studygroup.member.dto;

import java.util.List;

public class StudyGroupActivityPageResponse {

    private List<StudyGroupActivityItemResponse> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public StudyGroupActivityPageResponse() {
    }

    public StudyGroupActivityPageResponse(
            List<StudyGroupActivityItemResponse> content,
            int page,
            int size,
            int totalPages,
            long totalElements
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<StudyGroupActivityItemResponse> getContent() {
        return content;
    }

    public void setContent(List<StudyGroupActivityItemResponse> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
