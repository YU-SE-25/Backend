package com.unide.backend.domain.studygroup.member.dto;

public class StudyGroupCapacityDto {

    private int max;
    private int current;
    private int waitlisted;

    public StudyGroupCapacityDto(int max, int current, int waitlisted) {
        this.max = max;
        this.current = current;
        this.waitlisted = waitlisted;
    }

    public int getMax() {
        return max;
    }

    public int getCurrent() {
        return current;
    }

    public int getWaitlisted() {
        return waitlisted;
    }
}
