package com.usetech.dvente.repositories.users;


import com.usetech.dvente.entities.users.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findFirstByPhoneNumberAndVerifiedFalseOrderByIdDesc(String phoneNumber);
}
