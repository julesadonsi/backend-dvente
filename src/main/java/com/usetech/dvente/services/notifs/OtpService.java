package com.usetech.dvente.services.notifs;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.entities.users.VerificationCode;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.repositories.users.VerificationCodeRepository;
import com.usetech.dvente.responses.users.VerificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final VerificationCodeRepository repository;
    private final UserRepository userRepository;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @Value("${otp.expiration.minutes:5}")
    private int expirationMinutes;

    private static final int MAX_ATTEMPTS = 3;

    public VerificationResponse sendOtp(String phoneNumber) {

        Optional<VerificationCode> existingCode =
                repository.findFirstByPhoneNumberAndVerifiedFalseOrderByIdDesc(phoneNumber);

        Optional<User> user = userRepository.findByPhone(phoneNumber);
        if (user.isPresent()) {
            throw new IllegalArgumentException("User already has an unverified code");
        }

        if (existingCode.isPresent()) {
            VerificationCode oldCode = existingCode.get();
            oldCode.setVerified(true);
            repository.save(oldCode);
        }

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(expirationMinutes);
        VerificationCode verificationCode = new VerificationCode(phoneNumber, otp, expiryTime);
        repository.save(verificationCode);

        String messageText = "Your verification code is: " + otp +
                ". Valid for " + expirationMinutes + " minutes.";

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(fromPhoneNumber),
                messageText
        ).create();

        return new VerificationResponse(true, "OTP sent successfully");
    }


    public VerificationResponse verifyOtp(String phoneNumber, String code,  String email) {
        Optional<VerificationCode> optionalCode =
                repository.findFirstByPhoneNumberAndVerifiedFalseOrderByIdDesc(phoneNumber);

        if (optionalCode.isEmpty()) {
            return new VerificationResponse(false, "No verification code found");
        }

        VerificationCode verificationCode = optionalCode.get();

        // Check attempts
        if (verificationCode.getAttempts() >= MAX_ATTEMPTS) {
            return new VerificationResponse(false, "Maximum attempts exceeded. Request a new code.");
        }

        // Increment attempts
        verificationCode.setAttempts(verificationCode.getAttempts() + 1);
        repository.save(verificationCode);

        // Check expiry
        if (LocalDateTime.now().isAfter(verificationCode.getExpiryTime())) {
            return new VerificationResponse(false, "Verification code has expired");
        }

        // Verify code
        if (!verificationCode.getCode().equals(code)) {
            return new VerificationResponse(false,
                    "Invalid code. Attempts remaining: " + (MAX_ATTEMPTS - verificationCode.getAttempts()));
        }

        // Mark as verified
        verificationCode.setVerified(true);
        repository.save(verificationCode);
        System.out.println(email);
        //Update user
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setPhoneConfirmed(true);
            user.get().setPhone(phoneNumber);
            userRepository.save(user.get());
        } else {
            return new VerificationResponse(false, "User not found");
        }

        return new VerificationResponse(true, "Phone number verified successfully");
    }

    private String generateOtp() {
        Random random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
