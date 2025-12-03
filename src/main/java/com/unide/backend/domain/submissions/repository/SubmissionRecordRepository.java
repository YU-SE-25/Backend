package com.unide.backend.domain.submissions.repository;

import com.unide.backend.domain.submissions.entity.SubmissionRecord;
import com.unide.backend.domain.submissions.entity.Submissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRecordRepository extends JpaRepository<SubmissionRecord, Long> {
    List<SubmissionRecord> findAllBySubmissionOrderByTestCaseIndexAsc(Submissions submission);
}
