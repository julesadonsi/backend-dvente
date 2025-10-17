package com.usetech.dvente.repositories.users;

import com.usetech.dvente.entities.users.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository  extends JpaRepository<EmailVerification, UUID> {

    List<EmailVerification> findByEmail(String email);
    Optional<EmailVerification> findByEmailAndCode(String email, String code);
    Optional<EmailVerification > findFirstByEmailOrderByCreatedAtDesc(String email);

    List<EmailVerification> findByExpiredAtBefore(LocalDateTime expiredAt);

    boolean existsByEmailAndCodeAndIsUsedFalseAndExpiredAtAfter(
            String email, String code, LocalDateTime expiredAt
    );
}
