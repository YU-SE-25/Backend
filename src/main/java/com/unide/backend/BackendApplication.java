package com.unide.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.beans.BeanProperty;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@EnableJpaAuditing
@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner init(@Value("${file.upload-dir}") String uploadDir) {
        return args -> {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                Files.createDirectories(Paths.get(uploadDir));
                System.out.println("Created upload directory: " + uploadDir);
            }
        };
    }
}
