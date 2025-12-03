package com.unide.backend.domain.analysis.repository;

import com.unide.backend.domain.analysis.entity.SubmissionComplexity;
import com.unide.backend.domain.submissions.entity.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubmissionComplexityRepository extends JpaRepository<SubmissionComplexity, Long> {
    Optional<SubmissionComplexity> findBySubmission(Submissions submission);
}
