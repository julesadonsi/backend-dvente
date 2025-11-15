package com.usetech.dvente.utils;

import com.usetech.dvente.entities.users.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class MemoryCodeStorage {

    private static class CodeEntry {
        @Getter
        private final String code;
        private final LocalDateTime expirationTime;

        public CodeEntry(String code) {
            this.code = code;
            this.expirationTime = LocalDateTime.now().plusMinutes(5);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }

    }

    private final ConcurrentHashMap<String, CodeEntry> verificationCodes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::cleanupExpiredCodes, 1, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
    }

    public String generateCodeAndStoreInMemory(User user) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        String email = user.getEmail();
        verificationCodes.put(email, new CodeEntry(code));
        return code;
    }

    public boolean verifyCode(String email, String code) {
        CodeEntry entry = verificationCodes.get(email);
        if (entry == null || entry.isExpired()) {
            verificationCodes.remove(email);
            return false;
        }
        return entry.getCode().equals(code);
    }

    public void removeCode(String email) {
        verificationCodes.remove(email);
    }

    private void cleanupExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
