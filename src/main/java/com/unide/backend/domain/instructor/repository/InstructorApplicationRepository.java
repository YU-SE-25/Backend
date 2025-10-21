// InstructorApplication 엔티티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.instructor.repository;

import com.unide.backend.domain.instructor.entity.ApplicationStatus;
import com.unide.backend.domain.instructor.entity.InstructorApplication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InstructorApplicationRepository extends JpaRepository<InstructorApplication, Long> {
    // 특정 상태의 지원 목록을 페이지별로 조회 (사용자 정보 포함 Fetch Join)
    @Query("SELECT ia FROM InstructorApplication ia JOIN FETCH ia.user WHERE ia.status = :status")
    Page<InstructorApplication> findByStatusWithUser(ApplicationStatus status, Pageable pageable);

    // 사용자 ID로 지원서를 조회하는 메서드 (중복 지원 방지 등에 사용)
    Optional<InstructorApplication> findByUserId(Long userId);

    // ID로 지원서 상세 정보 조회 (지원자 및 처리자 정보 포함 Fetch Join)
    @Query("SELECT ia FROM InstructorApplication ia " +
           "JOIN FETCH ia.user " +
           "LEFT JOIN FETCH ia.processor " +
           "WHERE ia.id = :applicationId")
    Optional<InstructorApplication> findByIdWithDetails(@Param("applicationId") Long applicationId);
}
