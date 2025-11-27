package com.unide.backend.domain.studygroup.problem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupRepository;
import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemItemDto;
import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemListDetailDto;
import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemListRequest;
import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemListSummaryDto;
import com.unide.backend.domain.studygroup.problem.entity.StudyGroupProblem;
import com.unide.backend.domain.studygroup.problem.entity.StudyGroupProblemList;
import com.unide.backend.domain.studygroup.problem.repository.StudyGroupProblemListRepository;
import com.unide.backend.domain.studygroup.problem.repository.StudyGroupProblemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupProblemService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupProblemListRepository problemListRepository;
    private final StudyGroupProblemRepository studyGroupProblemRepository;
    private final ProblemsRepository problemsRepository;

    /**
     * 지정된 문제 목록 전체 조회
     */
    @Transactional(readOnly = true)
    public List<StudyGroupProblemListSummaryDto> getProblemLists(Long groupId) {

        List<StudyGroupProblemList> lists = problemListRepository.findByGroup_GroupId(groupId);

        return lists.stream()
                .map(list -> {

                    List<Long> problemIds = list.getProblems().stream()
                            .map(sp -> sp.getProblem().getId())  // Problems PK = id
                            .collect(Collectors.toList());

                    return StudyGroupProblemListSummaryDto.builder()
                            .problemListId(list.getId())
                            .listTitle(list.getListTitle())
                            .dueDate(list.getDueDate())
                            .problems(problemIds)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 문제 리스트 조회
     */
    @Transactional(readOnly = true)
    public StudyGroupProblemListDetailDto getProblemList(Long groupId, Long problemListId, Long userId) {

        StudyGroupProblemList list = problemListRepository.findById(problemListId)
                .orElseThrow(() -> new IllegalArgumentException("문제 리스트가 존재하지 않습니다."));

        StudyGroup group = list.getGroup();
        if (!group.getGroupId().equals(groupId)) { // groupId 기준으로 비교
            throw new IllegalArgumentException("해당 스터디 그룹에 속한 리스트가 아닙니다.");
        }

        List<StudyGroupProblemItemDto> items = list.getProblems().stream()
                .map(sp -> {
                    Problems problem = sp.getProblem();
                    String userStatus = "NOT_SUBMITTED"; // TODO: 제출 여부 연동

                    return StudyGroupProblemItemDto.builder()
                            .problemId(problem.getId())
                            .problemTitle(problem.getTitle())
                            .userStatus(userStatus)
                            .build();
                })
                .collect(Collectors.toList());

        return StudyGroupProblemListDetailDto.builder()
                .problemListId(list.getId())
                .listTitle(list.getListTitle())
                .dueDate(list.getDueDate())
                .problems(items)
                .build();
    }

    /**
     * 문제 리스트 생성
     */
    public StudyGroupProblemListDetailDto createProblemList(Long groupId, StudyGroupProblemListRequest request) {

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹이 존재하지 않습니다."));

        StudyGroupProblemList list = StudyGroupProblemList.builder()
                .group(group)
                .listTitle(request.getListTitle())
                .dueDate(request.getDueDate())
                .build();

        List<StudyGroupProblem> problems = request.getProblems().stream()
                .map(problemId -> {
                    Problems problem = problemsRepository.findById(problemId)
                            .orElseThrow(() -> new IllegalArgumentException("문제가 존재하지 않습니다. id=" + problemId));

                    return StudyGroupProblem.builder()
                            .problemList(list)
                            .problem(problem)
                            .build();
                })
                .collect(Collectors.toList());

        list.getProblems().addAll(problems);
        StudyGroupProblemList saved = problemListRepository.save(list);

        return getProblemList(groupId, saved.getId(), null);
    }

    /**
     * 문제 리스트 수정
     */
    public StudyGroupProblemListDetailDto updateProblemList(Long groupId, Long problemListId,
                                                            StudyGroupProblemListRequest request) {

        StudyGroupProblemList list = problemListRepository.findById(problemListId)
                .orElseThrow(() -> new IllegalArgumentException("문제 리스트가 존재하지 않습니다."));

        if (!list.getGroup().getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 스터디 그룹에 속한 리스트가 아닙니다.");
        }

        list.setListTitle(request.getListTitle());
        list.setDueDate(request.getDueDate());

        list.getProblems().clear();
        studyGroupProblemRepository.deleteByProblemList_Id(problemListId);

        List<StudyGroupProblem> newProblems = request.getProblems().stream()
                .map(problemId -> {
                    Problems problem = problemsRepository.findById(problemId)
                            .orElseThrow(() -> new IllegalArgumentException("문제가 존재하지 않습니다. id=" + problemId));

                    return StudyGroupProblem.builder()
                            .problemList(list)
                            .problem(problem)
                            .build();
                })
                .collect(Collectors.toList());

        list.getProblems().addAll(newProblems);

        return getProblemList(groupId, problemListId, null);
    }

    /**
     * 문제 리스트 삭제
     */
    public void deleteProblemList(Long groupId, Long problemListId) {

        StudyGroupProblemList list = problemListRepository.findById(problemListId)
                .orElseThrow(() -> new IllegalArgumentException("문제 리스트가 존재하지 않습니다."));

        if (!list.getGroup().getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 스터디 그룹에 속한 리스트가 아닙니다.");
        }

        problemListRepository.delete(list);
    }
}
