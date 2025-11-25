// Docker 컨테이너를 이용해 안전하게 코드를 실행하는 서비스

package com.unide.backend.domain.submissions.service;

import com.unide.backend.domain.submissions.dto.CodeRunRequestDto;
import com.unide.backend.domain.submissions.dto.CodeRunResponseDto;
import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DockerService {
    private final DockerClient dockerClient;
    private static final String DOCKER_IMAGE = "unide/code-executor";

    public DockerService() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        ZerodepDockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    public CodeRunResponseDto runCode(CodeRunRequestDto request) {
        String containerId = null;
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(DOCKER_IMAGE)
                    .withHostConfig(HostConfig.newHostConfig().withMemory(512 * 1024 * 1024L))
                    .withTty(true)
                    .exec();
            containerId = container.getId();

            dockerClient.startContainerCmd(containerId).exec();

            String fileName = getFileName(request.getLanguage());
            String fileCmd = "echo \"" + request.getCode().replace("\"", "\\\"").replace("$", "\\$") + "\" > " + fileName;
            execCommand(containerId, "sh", "-c", fileCmd);

            if (needsCompilation(request.getLanguage())) {
                String compileCmd = getCompileCommand(request.getLanguage(), fileName);
                ExecResult compileResult = execCommand(containerId, "sh", "-c", compileCmd);
                if (compileResult.exitCode != 0) {
                    return CodeRunResponseDto.builder()
                            .isSuccess(false)
                            .error("Compilation Error:\n" + compileResult.stderr)
                            .status(SubmissionStatus.CE)
                            .build();
                }
            }

            String runCmd = getRunCommand(request.getLanguage(), fileName, request.getInput());
            long startTime = System.currentTimeMillis();
            int timeLimit = 5;
            ExecResult runResult = execute(containerId, "sh", "-c", runCmd, timeLimit);
            long endTime = System.currentTimeMillis();

            return CodeRunResponseDto.builder()
                    .isSuccess(runResult.status == SubmissionStatus.CA)
                    .output(runResult.output)
                    .error(runResult.status == SubmissionStatus.RE ? runResult.output : null)
                    .executionTimeMs(endTime - startTime)
                    .status(runResult.status)
                    .build();

        } catch (Exception e) {
            log.error("Docker execution failed", e);
            return CodeRunResponseDto.builder()
                    .isSuccess(false)
                    .error("System Error: " + e.getMessage())
                    .status(SubmissionStatus.RE)
                    .build();
        } finally {
            if (containerId != null) {
                try {
                    dockerClient.removeContainerCmd(containerId).withForce(true).exec();
                } catch (Exception e) {
                    log.warn("Failed to remove container {}", containerId);
                }
            }
        }
    }

    private ExecResult execute(String containerId, String shell, String option, String command, int timeLimit) throws InterruptedException {
        ExecCreateCmdResponse execResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(shell, option, command)
                .exec();

        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();

        dockerClient.execStartCmd(execResponse.getId())
                .exec(new com.github.dockerjava.api.async.ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(com.github.dockerjava.api.model.Frame frame) {
                        try {
                            switch (frame.getStreamType()) {
                                case STDOUT -> stdoutStream.write(frame.getPayload());
                                case STDERR -> stderrStream.write(frame.getPayload());
                            }
                        } catch (Exception e) {
                            log.error("Stream error", e);
                        }
                    }
                }).awaitCompletion(timeLimit, TimeUnit.SECONDS);

        Long exitCode = dockerClient.inspectExecCmd(execResponse.getId()).exec().getExitCodeLong();
        String stdout = stdoutStream.toString(StandardCharsets.UTF_8).trim();
        String stderr = stderrStream.toString(StandardCharsets.UTF_8).trim();

        if (exitCode == null) { 
            return ExecResult.builder()
                    .status(SubmissionStatus.TLE)
                    .output("Time Limit Exceeded")
                    .exitCode(-1)
                    .build();
        }
        
        if (exitCode != 0) {
            return ExecResult.builder()
                    .status(SubmissionStatus.RE)
                    .output(stderr.isEmpty() ? "Runtime Error (Exit code: " + exitCode + ")" : stderr)
                    .exitCode(exitCode.intValue())
                    .build();
        }

        return ExecResult.builder()
                .status(SubmissionStatus.CA)
                .output(stdout)
                .exitCode(0)
                .build();
    }
    
    private ExecResult execCommand(String containerId, String... command) throws InterruptedException {
        return execute(containerId, command[0], command[1], command[2], 10);
    }

    @Getter
    @Builder
    private static class ExecResult {
        private String output;
        private String stderr;
        private int exitCode;
        private SubmissionStatus status;
    }
    
    private String getFileName(SubmissionLanguage language) {
        return switch (language) {
            case JAVA -> "Main.java";
            case PYTHON -> "solution.py";
            case CPP -> "main.cpp";
            default -> "script.txt";
        };
    }

    private boolean needsCompilation(SubmissionLanguage language) {
        return language == SubmissionLanguage.JAVA || language == SubmissionLanguage.CPP;
    }

    private String getCompileCommand(SubmissionLanguage language, String fileName) {
        return switch (language) {
            case JAVA -> "javac " + fileName;
            case CPP -> "g++ " + fileName + " -o main";
            default -> "";
        };
    }

    private String getRunCommand(SubmissionLanguage language, String fileName, String input) {
        String cmd = switch (language) {
            case JAVA -> "java Main";
            case PYTHON -> "python3 " + fileName;
            case CPP -> "./main";
            default -> "";
        };
        
        if (input != null && !input.isEmpty()) {
            return "echo \"" + input.replace("\"", "\\\"") + "\" | " + cmd;
        }
        return cmd;
    }
}