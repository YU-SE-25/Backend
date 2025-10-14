// 사용자 정보를 데이터베이스에서 관리하기 위한 JPA 리포지토리 인터페이스

package main.java.com.unide.backend.domain.user.repository;

import com.unide.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
