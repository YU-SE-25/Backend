package com.unide.backend.domain.submission.entity;

public enum ProgrammingLanguage {
    PYTHON("Python"),
    JAVA("Java"),
    CPP("C++"),
    C("C");

    private final String displayName;

    ProgrammingLanguage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
