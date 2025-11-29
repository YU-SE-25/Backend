// 문제 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.problems.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unide.backend.common.mail.MailService;

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
    private final MailService mailService;
    
    private final ProblemsRepository problemsRepository;
    private final SubmissionsRepository submissionsRepository;
    
    /** 상태별 문제 조회 (매니저용) */
    public Page<ProblemResponseDto> getProblemsByStatus(com.unide.backend.domain.problems.entity.ProblemStatus status, Pageable pageable) {
        return problemsRepository.findByStatus(status, pageable)
                .map(ProblemResponseDto::from);
    }

    /** 내가 만든 문제 조회 */
    public Page<ProblemResponseDto> getProblemsByCreator(Long userId, Pageable pageable) {
        return problemsRepository.findByCreatedById(userId, pageable)
                .map(ProblemResponseDto::from);
    }
    
    /** 문제 등록 */
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
                .status(requestDto.getStatus())   
                .tags(requestDto.getTags())
                .hint(requestDto.getHint())
                .source(requestDto.getSource())
                .build();
        
        Problems savedProblem = problemsRepository.save(problem);
        return savedProblem.getId();
    }
    
    /** 문제 수정 */
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
        if (requestDto.getStatus() != null) {
            problem.updateStatus(requestDto.getStatus());
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

        // 매니저가 수정한 경우 등록한 강사에게 이메일 전송
        if (user.getRole() == UserRole.MANAGER) {
            User creator = problem.getCreatedBy();
            String to = creator.getEmail();
            String subject = "[UnIDE] 등록한 문제가 수정되었습니다";
            String body = String.format("안녕하세요, %s님.\n\n등록하신 문제 '%s'가 매니저에 의해 수정되었습니다.\n\n확인 부탁드립니다.", creator.getName(), problem.getTitle());
            mailService.sendEmail(to, subject, body);
        }
    }
    
    /** 승인된 문제 조회 */
    public Page<ProblemResponseDto> getProblems(Pageable pageable) {
        return problemsRepository.findByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable)
                .map(ProblemResponseDto::from);
    }
    
    /** 승인된 문제 조회 (사용자용) */
    public Page<ProblemResponseDto> getProblems(Long userId, Pageable pageable) {
        return problemsRepository.findByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable)
                .map(problem -> {
                    if (userId != null) {
                        boolean isSolved = problemsRepository.isSolvedByUser(userId, problem.getId());
                        return ProblemResponseDto.from(problem, isSolved);
                    }
                    return ProblemResponseDto.from(problem);
                });
    }
    
    /** 승인된 문제 검색 (사용자용) */
    public Page<ProblemResponseDto> searchProblems(Long userId, String title, ProblemDifficulty difficulty, Pageable pageable) {
        Page<Problems> problems;
        if (title != null && difficulty != null) {
            problems = problemsRepository.findByTitleContainingAndDifficultyAndStatus(title, difficulty, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
        } else if (title != null) {
            problems = problemsRepository.findByTitleContainingAndStatus(title, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
        } else if (difficulty != null) {
            problems = problemsRepository.findByDifficultyAndStatus(difficulty, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
        } else {
            problems = problemsRepository.findByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
        }
        return problems.map(problem -> {
            if (userId != null) {
                boolean isSolved = problemsRepository.isSolvedByUser(userId, problem.getId());
                return ProblemResponseDto.from(problem, isSolved);
            }
            return ProblemResponseDto.from(problem);
        });
    }
    
    /** 문제 상세 조회 */
    @Transactional
    public ProblemDetailResponseDto getProblemDetail(Long problemId, PrincipalDetails principalDetails) {
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));

        // 일반 사용자는 APPROVED 상태만 조회 가능, 작성자나 매니저는 예외
        boolean isOwner = false;
        boolean isManager = false;
        if (principalDetails != null) {
            User user = principalDetails.getUser();
            isOwner = problem.getCreatedBy().getId().equals(user.getId());
            isManager = user.getRole() == UserRole.MANAGER;
        }
        if (problem.getStatus() != com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED && !isOwner && !isManager) {
            throw new IllegalArgumentException("승인된 문제만 조회할 수 있습니다");
        }

        problem.increaseViewCount();

        Long totalSubmissions = submissionsRepository.countByProblemId(problemId);
        Long acceptedSubmissions = submissionsRepository.countAcceptedByProblemId(problemId);

        boolean canEdit = isOwner || isManager;
        return ProblemDetailResponseDto.from(problem, totalSubmissions, acceptedSubmissions, canEdit);
    }
    
    /** 문제 삭제 */
    @Transactional
    public void deleteProblem(Long problemId) {
        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));
        
        problemsRepository.delete(problem);
    }
    
    /** 문제 삭제 권한 검증 */
    private void validateInstructorOrManager(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR && user.getRole() != UserRole.MANAGER) {
            throw new IllegalArgumentException("문제 등록/수정 권한이 없습니다. (강사 또는 관리자만 가능)");
        }
    }
}
