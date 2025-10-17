package com.usetech.dvente.schedulers;

import com.usetech.dvente.services.users.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private final EmailVerificationService emailVerificationService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredEmailVerifications() {
        log.info("Nettoyage des vérifications d'email expirées");
        emailVerificationService.cleanupExpiredVerifications();
    }
}
