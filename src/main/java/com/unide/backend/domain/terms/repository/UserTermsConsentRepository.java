// 사용자 약관 동의 정보를 관리하는 JPA 리포지토리 인터페이스

package com.unide.backend.domain.terms.repository;

import com.unide.backend.domain.terms.entity.UserTermsConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsConsentRepository extends JpaRepository<UserTermsConsent, Long> {
    
}
