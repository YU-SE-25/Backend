// 문제 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.problems.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.unide.backend.domain.problems.dto.ProblemCreateRequestDto;
import com.unide.backend.domain.problems.dto.ProblemDetailResponseDto;
import com.unide.backend.domain.problems.dto.ProblemResponseDto;
import com.unide.backend.domain.problems.dto.ProblemUpdateRequestDto;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.problems.repository.TestCaseRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.problems.entity.TestCase;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {
    @Value("${app.upload.testcase-dir}")
    private String testcaseUploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
    
    private final ProblemsRepository problemsRepository;
    private final SubmissionsRepository submissionsRepository;
    private final TestCaseRepository testCaseRepository;
    
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
    public Long createProblem(User user, ProblemCreateRequestDto requestDto, MultipartFile testcaseFile) {

        String path = null;

        // 파일이 들어온 경우에만 저장
        if (testcaseFile != null && !testcaseFile.isEmpty()) {
            path = saveTestcaseFile(testcaseFile);
        }

        Problems problem = Problems.builder()
                .createdBy(user)
                .title(requestDto.getTitle())
                .summary(requestDto.getSummary())
                .description(requestDto.getDescription())
                .inputOutputExample(requestDto.getInputOutputExample())
                .difficulty(requestDto.getDifficulty())
                .timeLimit(requestDto.getTimeLimit())
                .memoryLimit(requestDto.getMemoryLimit())
                .status(requestDto.getStatus())
                .tags(requestDto.getTags())
                .hint(requestDto.getHint())
                .source(requestDto.getSource())
                .testcaseFilePath(path)
                .build();

        problemsRepository.save(problem);

        // 테스트케이스 저장
        if (path != null) {
            saveTestCasesFromFile(problem, path); 
        } else if (requestDto.getTestCases() != null) {
            for (com.unide.backend.domain.problems.dto.TestCaseDto tcDto : requestDto.getTestCases()) {
                if (tcDto.getInput() != null && tcDto.getOutput() != null) {
                    testCaseRepository.save(
                        com.unide.backend.domain.problems.entity.TestCase.builder()
                            .problem(problem)
                            .input(tcDto.getInput())
                            .output(tcDto.getOutput())
                            .build()
                    );
                }
            }
        }
        return problem.getId();
    }

    /** 문제 수정 */
    @Transactional
    public void updateProblem(User user, Long problemId, ProblemUpdateRequestDto dto, MultipartFile newFile) {

        validateInstructorOrManager(user);

        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다."));

        // 값 수정
        if (dto.getTitle() != null) problem.updateTitle(dto.getTitle());
        if (dto.getSummary() != null) problem.updateSummary(dto.getSummary());
        if (dto.getDescription() != null) problem.updateDescription(dto.getDescription());
        if (dto.getInputOutputExample() != null) problem.updateInputOutputExample(dto.getInputOutputExample());
        if (dto.getDifficulty() != null) problem.updateDifficulty(dto.getDifficulty());
        if (dto.getTimeLimit() != null) problem.updateTimeLimit(dto.getTimeLimit());
        if (dto.getMemoryLimit() != null) problem.updateMemoryLimit(dto.getMemoryLimit());
        if (dto.getStatus() != null) problem.updateStatus(dto.getStatus());
        if (dto.getHint() != null) problem.updateHint(dto.getHint());
        if (dto.getSource() != null) problem.updateSource(dto.getSource());
        if (dto.getTags() != null) problem.updateTags(dto.getTags());

        // 파일이 새로 들어오면 저장 후 경로만 바꾸기
        if (newFile != null && !newFile.isEmpty()) {
            String newPath = saveTestcaseFile(newFile);
            problem.updateTestcaseFilePath(newPath);
        }

        // 테스트케이스 수정: 입력이 없으면 기본값 저장
        List<com.unide.backend.domain.problems.dto.TestCaseDto> testCases = dto.getTestCases();
        testCaseRepository.deleteByProblem(problem);
        if (testCases == null || testCases.isEmpty()) {
            // 기본 테스트케이스 저장
            testCaseRepository.save(
                com.unide.backend.domain.problems.entity.TestCase.builder()
                    .problem(problem)
                    .input("입력 없음")
                    .output("출력 없음")
                    .build()
            );
        } else {
            for (com.unide.backend.domain.problems.dto.TestCaseDto tcDto : testCases) {
                if (tcDto.getInput() != null && tcDto.getOutput() != null) {
                    testCaseRepository.save(
                        com.unide.backend.domain.problems.entity.TestCase.builder()
                            .problem(problem)
                            .input(tcDto.getInput())
                            .output(tcDto.getOutput())
                            .build()
                    );
                }
            }
        }

        if(user.getRole() == UserRole.MANAGER) {
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
                helper.setSubject("[Unide] 문제가 수정 안내"); // 제목
                helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

                mailSender.send(mimeMessage); // 최종 발송
            } catch (MessagingException e) {
                throw new RuntimeException("이메일 발송에 실패했습니다.", e);
            }
        }
    }
    
    private String saveTestcaseFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 폴더 준비
            Path uploadPath = Paths.get(testcaseUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // 파일명 정리
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            if (originalFilename.contains("..")) {
                throw new IllegalArgumentException("파일명에 '..' 문자를 포함할 수 없습니다.");
            }

            // UUID로 중복 방지
            String newFilename = UUID.randomUUID() + "_" + originalFilename;

            // 실제 저장 위치
            Path targetLocation = uploadPath.resolve(newFilename);

            // 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // ⭐⭐⭐ DB에는 URL 경로만 저장 (프론트 접근 가능)
            return baseUrl + "/uploads/testcases/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException("테스트케이스 파일 저장 실패: " + e.getMessage(), e);
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
                //String summary = problem.getDescription() != null ? problem.getDescription().substring(0, Math.min(50, problem.getDescription().length())) : "";
                String summary = problem.getSummary();
                // 푼 사람 수(정답 제출한 유저 수)
                Long acceptedCount = submissionsRepository.countAcceptedByProblemId(problem.getId());
                Long distinctSolvedUsers = submissionsRepository.countDistinctSolvedUsersByProblemId(problem.getId());
                Integer solverCount = distinctSolvedUsers != null ? distinctSolvedUsers.intValue() : 0;
                // 정답률
                Long totalCount = submissionsRepository.countByProblemId(problem.getId());
                Double correctRate;

                if (totalCount == null || totalCount == 0L) {
                    correctRate = 0.0;
                } else {
                    correctRate = acceptedCount.doubleValue() / totalCount.doubleValue();
                    if (correctRate > 100.0) {
                        correctRate = 100.0;
                    }
                }
                
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
            //String summary = problem.getDescription() != null ? problem.getDescription().substring(0, Math.min(50, problem.getDescription().length())) : "";
            String summary = problem.getSummary();
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
			context.setVariable("problemDetailUrl", "http://localhost:3000/problems/detail/" + problem.getId());

			String html = templateEngine.process("problem-approved-email.html", context);

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
			context.setVariable("problemDetailUrl", "http://localhost:3000/problems/detail/" + problem.getId());

			String html = templateEngine.process("problem-rejected-email.html", context);

			helper.setTo(creator.getEmail()); // 받는 사람
			helper.setSubject("[Unide] 문제 반려 안내"); // 제목
	
            helper.setText(html, true); // 본문 (true는 이 내용이 HTML임을 의미)

			mailSender.send(mimeMessage); // 최종 발송
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 발송에 실패했습니다.", e);
		}
    }

    @Transactional
    private void saveTestCasesFromFile(Problems problem, String filePath) {
        String cleanFilePath = filePath.replace(baseUrl + "/uploads/testcases/", "");
        Path path = Paths.get(testcaseUploadDir, cleanFilePath);

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("Input\\s*\\n(.*?)\\nOutput\\s*\\n(.*?)(?=\\nInput|\\z)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String input = matcher.group(1).trim();
                String output = matcher.group(2).trim();

                if (!input.isEmpty() && !output.isEmpty()) {
                    TestCase testCase = TestCase.builder()
                        .problem(problem)
                        .input(input)
                        .output(output)
                        .build();
                    testCaseRepository.save(testCase);
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("테스트케이스 파일 분석 및 로딩 실패: " + e.getMessage(), e);
        }
    }
}