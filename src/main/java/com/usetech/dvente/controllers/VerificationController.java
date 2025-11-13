package com.usetech.dvente.controllers;


import com.usetech.dvente.requests.users.SendOtpRequest;
import com.usetech.dvente.requests.users.VerifyOtpRequest;
import com.usetech.dvente.responses.users.VerificationResponse;
import com.usetech.dvente.services.notifs.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final OtpService otpService;

    @PostMapping("/send-otp")
    private ResponseEntity<VerificationResponse> sendOtp(@RequestBody SendOtpRequest request) {
        VerificationResponse verificationResponse = otpService.sendOtp(request.getPhoneNumber());
        return ResponseEntity.ok(verificationResponse);
    }

    @PostMapping("/verify-otp")
    private ResponseEntity<VerificationResponse> verifyOtp
            (@RequestBody VerifyOtpRequest request, Authentication authentication) {
        String  email = authentication.getName();
        VerificationResponse verificationResponse = otpService.verifyOtp(request.getPhoneNumber(), request.getCode(), email);
        return ResponseEntity.ok(verificationResponse);
    }

}
