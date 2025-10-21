package com.usetech.dvente.services.auth;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    @CachePut(value = "verificationCodes", key = "'email:' + #email")
    public String saveEmailCode(String email, String code) {
        return code;
    }

    @Cacheable(value = "verificationCodes", key = "'email:' + #email")
    public String getEmailCode(String email) {
        return null;
    }

    @CacheEvict(value = "verificationCodes", key = "'email:' + #email")
    public void removeEmailCode(String email) {}
}


