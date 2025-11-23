// 문제 태그를 나타내는 Enum

package com.unide.backend.domain.problems.entity;

public enum ProblemTag {
    IMPLEMENTATION("구현"),
    SORTING("정렬"),
    PRIORITY_QUEUE("우선순위 큐"),
    GRAPH("그래프"),
    DFS("DFS"),
    BFS("BFS"),
    DP("동적 프로그래밍"),
    GREEDY("그리디"),
    BINARY_SEARCH("이분 탐색"),
    TWO_POINTER("투 포인터"),
    SLIDING_WINDOW("슬라이딩 윈도우"),
    STACK("스택"),
    QUEUE("큐"),
    HASH("해시"),
    STRING("문자열"),
    MATH("수학"),
    SIMULATION("시뮬레이션"),
    BRUTE_FORCE("브루트포스"),
    BACKTRACKING("백트래킹"),
    TREE("트리");
    
    private final String korean;
    
    ProblemTag(String korean) {
        this.korean = korean;
    }
    
    public String getKorean() {
        return korean;
    }
}
