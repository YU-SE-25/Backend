// 문제 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.problems.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.unide.backend.domain.problems.dto.ProblemCreateRequestDto;
import com.unide.backend.domain.problems.dto.ProblemDetailResponseDto;
import com.unide.backend.domain.problems.dto.ProblemResponseDto;
import com.unide.backend.domain.problems.dto.ProblemUpdateRequestDto;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.entity.TestCase;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.problems.repository.TestCaseRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {
    private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
    
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

        // 1. 파일 저장
        MultipartFile testcaseFile = requestDto.getTestcaseFile();
        String testcaseFilePath = saveTestcaseFile(testcaseFile);

        // 2. 엔티티 생성 시 경로 할당
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
                .testcaseFilePath(testcaseFilePath)
                .build();

        Problems savedProblem = problemsRepository.save(problem);

        // 테스트케이스 저장
        try {
            parseAndSaveTestCases(requestDto.getTestcaseFile(), savedProblem);
        } catch (IOException e) {
            throw new RuntimeException("테스트케이스 파싱/저장 중 오류 발생", e);
        }

        return savedProblem.getId();
    }

    // 파일 저장 메서드 예시
    private String saveTestcaseFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("테스트케이스 파일이 필요합니다.");
        }
        String uploadDir = "/uploads/testcases/";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("테스트케이스 파일 저장 실패", e);
        }
        return dest.getPath();
    }

    @Autowired
    private TestCaseRepository testCaseRepository;

    private List<TestCase> parseAndSaveTestCases(MultipartFile file, Problems problem) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        String[] cases = content.split("===\\s*");
        List<TestCase> testCases = new ArrayList<>();
        for (String testCaseBlock : cases) {
            String[] io = testCaseBlock.split("---\\s*");
            if (io.length == 2) {
                TestCase testCase = TestCase.builder()
                    .problem(problem)
                    .input(io[0].trim())
                    .output(io[1].trim())
                    .build();
                testCases.add(testCaseRepository.save(testCase));
            }
        }
        return testCases;
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
        if (requestDto.getTestcaseFile() != null) {
            String testcaseFilePath = saveTestcaseFile(requestDto.getTestcaseFile());
            problem.updateTestcaseFilePath(testcaseFilePath);
            try {
                parseAndSaveTestCases(requestDto.getTestcaseFile(), problem);
            } catch (IOException e) {
                throw new RuntimeException("테스트케이스 파싱/저장 중 오류 발생", e);
            }
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
                try {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                    Context context = new Context();
                    context.setVariable("name", creator.getNickname());
                    context.setVariable("problemTitle", problem.getTitle());
                    context.setVariable("problemDetailUrl", "http://localhost:3000/problems/" + problem.getId());

                    String html = templateEngine.process("problem-modified-email.html", context);

                    helper.setTo(creator.getEmail()); // 받는 사람
                    helper.setSubject("[Unide] 문제가 수정되었습니다"); // 제목
                    helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

                    mailSender.send(mimeMessage); // 최종 발송
                } catch (MessagingException e) {
                    throw new RuntimeException("이메일 발송에 실패했습니다.", e);
                }
        }
    }
    
    /** 승인된 문제 조회 */
    public Page<ProblemResponseDto> getProblems(Pageable pageable) {
        return problemsRepository.findByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable)
                .map(ProblemResponseDto::from);
    }
    
    /** 승인된 문제 조회 (문제 리스트 전체 조회) */
    public Page<ProblemResponseDto> getProblems(Long userId, Pageable pageable) {
        return problemsRepository.findByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable)
            .map(problem -> {
                // 기본값: 안 푼 문제
                com.unide.backend.domain.problems.dto.UserStatus userStatus = com.unide.backend.domain.problems.dto.UserStatus.NOT_SOLVED;

                if (userId != null) {
                    Long correctCount = submissionsRepository.countAcceptedByProblemIdAndUserId(problem.getId(), userId);
                    Long wrongCount = submissionsRepository.countWrongByProblemIdAndUserId(problem.getId(), userId);

                    if (correctCount != null && correctCount > 0) {
                        userStatus = com.unide.backend.domain.problems.dto.UserStatus.CORRECT;
                    } else if (wrongCount != null && wrongCount > 0) {
                        userStatus = com.unide.backend.domain.problems.dto.UserStatus.INCORRECT;
                    }
                }

                // 문제 요약(설명 일부)
                String summary = problem.getDescription() != null ? problem.getDescription().substring(0, Math.min(50, problem.getDescription().length())) : "";
                // 푼 사람 수(정답 제출한 유저 수)
                Long acceptedCount = submissionsRepository.countAcceptedByProblemId(problem.getId());
                Integer solverCount = acceptedCount != null ? acceptedCount.intValue() : 0;
                // 정답률
                Long totalCount = submissionsRepository.countByProblemId(problem.getId());
                Double correctRate = (totalCount != null && totalCount > 0) ? (acceptedCount.doubleValue() / totalCount.doubleValue() * 100) : null;

                return ProblemResponseDto.from(problem, userStatus, summary, solverCount, correctRate);
            });
    }
    
    /** 승인된 문제 검색 (검색 조건 및 태그 포함) */
    public Page<ProblemResponseDto> searchProblems(Long userId, String title, ProblemDifficulty difficulty, java.util.List<String> tags, Pageable pageable) {
        Page<Problems> problems;
        java.util.List<com.unide.backend.domain.problems.entity.ProblemTag> tagEnums = null;
        if (tags != null && !tags.isEmpty()) {
            tagEnums = new java.util.ArrayList<>();
            for (String tag : tags) {
                try {
                    tagEnums.add(com.unide.backend.domain.problems.entity.ProblemTag.valueOf(tag));
                } catch (IllegalArgumentException e) {
                    // 잘못된 태그는 무시
                }
            }
        }
        if (tagEnums != null && !tagEnums.isEmpty()) {
            if (title != null && difficulty != null) {
                problems = problemsRepository.findByTagsInAndTitleContainingAndDifficultyAndStatus(tagEnums, title, difficulty, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            } else if (title != null) {
                problems = problemsRepository.findByTagsInAndTitleContainingAndStatus(tagEnums, title, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            } else if (difficulty != null) {
                problems = problemsRepository.findByTagsInAndDifficultyAndStatus(tagEnums, difficulty, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            } else {
                problems = problemsRepository.findByTagsInAndStatus(tagEnums, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            }
        } else {
            if (title != null && difficulty != null) {
                problems = problemsRepository.findByTitleContainingAndDifficultyAndStatus(title, difficulty, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            } else if (title != null) {
                problems = problemsRepository.findByTitleContainingAndStatus(title, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            } else if (difficulty != null) {
                problems = problemsRepository.findByDifficultyAndStatus(difficulty, com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            } else {
                problems = problemsRepository.findByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED, pageable);
            }
        }
        return problems.map(problem -> {
            com.unide.backend.domain.problems.dto.UserStatus userStatus = com.unide.backend.domain.problems.dto.UserStatus.NOT_SOLVED;
            if (userId != null) {
                Long correctCount = submissionsRepository.countAcceptedByProblemIdAndUserId(problem.getId(), userId);
                Long wrongCount = submissionsRepository.countWrongByProblemIdAndUserId(problem.getId(), userId);
                if (correctCount != null && correctCount > 0) {
                    userStatus = com.unide.backend.domain.problems.dto.UserStatus.CORRECT;
                } else if (wrongCount != null && wrongCount > 0) {
                    userStatus = com.unide.backend.domain.problems.dto.UserStatus.INCORRECT;
                }
            }
            String summary = problem.getDescription() != null ? problem.getDescription().substring(0, Math.min(50, problem.getDescription().length())) : "";
            Long acceptedCount = submissionsRepository.countAcceptedByProblemId(problem.getId());
            Integer solverCount = acceptedCount != null ? acceptedCount.intValue() : 0;
            Long totalCount = submissionsRepository.countByProblemId(problem.getId());
            Double correctRate = (totalCount != null && totalCount > 0) ? (acceptedCount.doubleValue() / totalCount.doubleValue() * 100) : null;
            return ProblemResponseDto.from(problem, userStatus, summary, solverCount, correctRate);
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

    /** 문제 ID 목록으로 문제 페이징 조회 */
    public Page<ProblemResponseDto> getProblemsByIds(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return Page.empty(pageable);
        }
        // 문제 엔티티 페이징 조회
        Page<Problems> problemsPage = problemsRepository.findAllById(ids, pageable);
        return problemsPage.map(ProblemResponseDto::from);
    }

    /** 문제 승인(수락) */
    @Transactional
    public void approveProblem(Long problemId) {
        Problems problem = problemsRepository.findById(problemId)
            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));
        problem.updateStatus(com.unide.backend.domain.problems.entity.ProblemStatus.APPROVED);
        
        // 등록자에게 메일 전송
        User creator = problem.getCreatedBy();
        try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

			Context context = new Context();
			context.setVariable("name", creator.getNickname());
			context.setVariable("problemTitle", problem.getTitle());
			context.setVariable("problemDetailUrl", "http://localhost:3000/problems/" + problem.getId());

			String html = templateEngine.process("reminder-email.html", context);

			helper.setTo(creator.getEmail()); // 받는 사람
			helper.setSubject("[Unide] 문제 승인 안내"); // 제목
	
            helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

			mailSender.send(mimeMessage); // 최종 발송
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 발송에 실패했습니다.", e);
		}
    }

    /** 문제 반려(거절) */
    @Transactional
    public void rejectProblem(Long problemId) {
        Problems problem = problemsRepository.findById(problemId)
            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다"));
        problem.updateStatus(com.unide.backend.domain.problems.entity.ProblemStatus.REJECTED);
        
        // 등록자에게 메일 전송
        User creator = problem.getCreatedBy();
        try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

			Context context = new Context();
			context.setVariable("name", creator.getNickname());
			context.setVariable("problemTitle", problem.getTitle());
			context.setVariable("problemDetailUrl", "http://localhost:3000/problems/" + problem.getId());

			String html = templateEngine.process("reminder-email.html", context);

			helper.setTo(creator.getEmail()); // 받는 사람
			helper.setSubject("[Unide] 문제 반려 안내"); // 제목
	
            helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

			mailSender.send(mimeMessage); // 최종 발송
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 발송에 실패했습니다.", e);
		}
    }
}