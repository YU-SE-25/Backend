// 문제 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.problems.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.problems.dto.ProblemCreateRequestDto;
import com.unide.backend.domain.problems.dto.ProblemDetailResponseDto;
import com.unide.backend.domain.problems.dto.ProblemResponseDto;
import com.unide.backend.domain.problems.dto.ProblemUpdateRequestDto;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {
    
    private final ProblemsRepository problemsRepository;
    private final SubmissionsRepository submissionsRepository;
    
    @Transactional
    public Long createProblem(User user, ProblemCreateRequestDto requestDto) {
        validateInstructorOrManager(user);
        
        Problems problem = Problems.builder()
                .createdBy(user)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .inputOutputExample(requestDto.getInputOutputExample())
                .difficulty(requestDto.getDifficulty())
                .timeLimit(requestDto.getTimeLimit())
                .memoryLimit(requestDto.getMemoryLimit())
                .visibility(requestDto.getVisibility())
                .tags(requestDto.getTags())
                .hint(requestDto.getHint())
                .source(requestDto.getSource())
                .build();
        
        Problems savedProblem = problemsRepository.save(problem);
        return savedProblem.getId();
    }
    
    @Transactional
    public void updateProblem(User user, Long problemId, ProblemUpdateRequestDto requestDto) {
        validateInstructorOrManager(user);
        
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));
        
        if (requestDto.getTitle() != null) {
            problem.updateTitle(requestDto.getTitle());
        }
        if (requestDto.getDescription() != null) {
            problem.updateDescription(requestDto.getDescription());
        }
        if (requestDto.getInputOutputExample() != null) {
            problem.updateInputOutputExample(requestDto.getInputOutputExample());
        }
        if (requestDto.getDifficulty() != null) {
            problem.updateDifficulty(requestDto.getDifficulty());
        }
        if (requestDto.getTimeLimit() != null) {
            problem.updateTimeLimit(requestDto.getTimeLimit());
        }
        if (requestDto.getMemoryLimit() != null) {
            problem.updateMemoryLimit(requestDto.getMemoryLimit());
        }
        if (requestDto.getVisibility() != null) {
            problem.updateVisibility(requestDto.getVisibility());
        }
        if (requestDto.getTags() != null) {
            problem.updateTags(requestDto.getTags());
        }
        if (requestDto.getHint() != null) {
            problem.updateHint(requestDto.getHint());
        }
        if (requestDto.getSource() != null) {
            problem.updateSource(requestDto.getSource());
        }
    }
    
    public Page<ProblemResponseDto> getProblems(Pageable pageable) {
        return problemsRepository.findAll(pageable)
                .map(ProblemResponseDto::from);
    }
    
    public Page<ProblemResponseDto> getProblems(Long userId, Pageable pageable) {
        return problemsRepository.findAll(pageable)
                .map(problem -> {
                    if (userId != null) {
                        boolean isSolved = problemsRepository.isSolvedByUser(userId, problem.getId());
                        return ProblemResponseDto.from(problem, isSolved);
                    }
                    return ProblemResponseDto.from(problem);
                });
    }
    
    public Page<ProblemResponseDto> searchProblems(Long userId, String title, ProblemDifficulty difficulty, Pageable pageable) {
        Page<Problems> problems;
        
        if (title != null && difficulty != null) {
            problems = problemsRepository.findByTitleContainingAndDifficulty(title, difficulty, pageable);
        } else if (title != null) {
            problems = problemsRepository.findByTitleContaining(title, pageable);
        } else if (difficulty != null) {
            problems = problemsRepository.findByDifficulty(difficulty, pageable);
        } else {
            problems = problemsRepository.findAll(pageable);
        }
        
        return problems.map(problem -> {
            if (userId != null) {
                boolean isSolved = problemsRepository.isSolvedByUser(userId, problem.getId());
                return ProblemResponseDto.from(problem, isSolved);
            }
            return ProblemResponseDto.from(problem);
        });
    }
    
    @Transactional
    public ProblemDetailResponseDto getProblemDetail(Long problemId, PrincipalDetails principalDetails) {
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));
        
        problem.increaseViewCount();
        
        Long totalSubmissions = submissionsRepository.countByProblemId(problemId);
        Long acceptedSubmissions = submissionsRepository.countAcceptedByProblemId(problemId);
        
        // 작성자 본인이거나 관리자인 경우 수정 가능
        boolean canEdit = false;
        if (principalDetails != null) {
            User user = principalDetails.getUser();
            boolean isOwner = problem.getCreatedBy().getId().equals(user.getId());
            boolean isManager = user.getRole() == UserRole.MANAGER;
            canEdit = isOwner || isManager;
        }
        
        return ProblemDetailResponseDto.from(problem, totalSubmissions, acceptedSubmissions, canEdit);
    }
    
    @Transactional
    public void deleteProblem(Long problemId) {
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));
        
        problemsRepository.delete(problem);
    }
    
    private void validateInstructorOrManager(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR && user.getRole() != UserRole.MANAGER) {
            throw new IllegalArgumentException("문제 등록/수정 권한이 없습니다. (강사 또는 관리자만 가능)");
        }
    }
}
