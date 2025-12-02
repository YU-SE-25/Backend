package com.unide.backend.domain.analysis.repository;

import com.unide.backend.domain.analysis.entity.SubmissionFlowchart;
import com.unide.backend.domain.submissions.entity.Submissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionFlowchartRepository extends JpaRepository<SubmissionFlowchart, Long> {
    Optional<SubmissionFlowchart> findBySubmission(Submissions submission);
}
