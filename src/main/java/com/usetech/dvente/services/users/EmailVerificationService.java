package com.usetech.dvente.services.users;

import com.usetech.dvente.entities.users.EmailVerification;
import com.usetech.dvente.repositories.users.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository verificationRepository;

    /**
     * Crée une nouvelle vérification email avec un code généré.
     */
    @Transactional
    public EmailVerification createVerification(String email) {
        invalidateOldVerifications(email);
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .isUsed(false)
                .build();
        
        verification.generateCode();
        
        return verificationRepository.save(verification);
    }

    /**
     * Invalide toutes les anciennes vérifications non utilisées pour un email.
     */
    @Transactional
    public void invalidateOldVerifications(String email) {
        List<EmailVerification> oldVerifications = verificationRepository.findByEmail(email);
        oldVerifications.forEach(v -> v.setUsed(true));
        verificationRepository.saveAll(oldVerifications);
    }

    /**
     * Vérifie si un code est valide pour un email donné.
     */
    public boolean isCodeValid(String email, String code) {
        Optional<EmailVerification> verification = 
            verificationRepository.findByEmailAndCode(email, code);
        
        return verification.isPresent() && verification.get().isValid();
    }

    /**
     * Marque un code comme utilisé.
     */
    @Transactional
    public void markAsUsed(String email, String code) {
        Optional<EmailVerification> verification = 
            verificationRepository.findByEmailAndCode(email, code);
        
        verification.ifPresent(v -> {
            v.setUsed(true);
            verificationRepository.save(v);
        });
    }

    /**
     * Nettoie les vérifications expirées (à exécuter périodiquement).
     */
    @Transactional
    public void cleanupExpiredVerifications() {
        List<EmailVerification> expired = 
            verificationRepository.findByExpiredAtBefore(LocalDateTime.now());
        
        if (!expired.isEmpty()) {
            verificationRepository.deleteAll(expired);
            log.info("Supprimé {} vérifications expirées", expired.size());
        }
    }
}
