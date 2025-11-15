package com.usetech.dvente.controllers.checkpoints;


import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.services.notifs.EmailService;
import com.usetech.dvente.utils.MemoryCodeStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users/checkpoint")
public class EmailCheckController {

    private final EmailService emailService;
    private final MemoryCodeStorage codeStorage;

    @GetMapping("/email/code")
    public ResponseEntity<Map<String, Object>> sentEmailVerificationCode(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String email = user.getEmail();

        Map<String, Object> response = new HashMap<>();

        try {
            String code = codeStorage.generateCodeAndStoreInMemory(user);
            emailService.sendEmailInfoAttemptChangeEmail(user, code);

            response.put("status", 200);
            response.put("message", "Email verification code sent successfully");
            response.put("data", email);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("status", 500);
            response.put("message", "Unable to send email information");
            response.put("error", ex.getMessage());
            response.put("data", email);

            return ResponseEntity.status(500).body(response);
        }
    }


    @PostMapping("email/verify")
    public ResponseEntity<?> verifyCode(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();

        String code = request.get("code");
        System.out.println("Received code: " + code);

        if (code == null || code.trim().isEmpty()) {
            response.put("status", 400);
            response.put("message", "Code is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (codeStorage.verifyCode(user.getEmail(), code.trim())) {
            codeStorage.removeCode(user.getEmail());
            response.put("status", 200);
            response.put("message", "Code verified successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", 400);
            response.put("message", "EXPIRED_OR_INVALID");
            return ResponseEntity.badRequest().body(response);
        }
    }
}