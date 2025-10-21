// UserPortfolioFile 엔티티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.instructor.repository;

import com.unide.backend.domain.instructor.entity.UserPortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPortfolioFileRepository extends JpaRepository<UserPortfolioFile, Long> {

}
