package com.usetech.dvente.controllers.users;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.requests.users.ChangePasswordRequest;
import com.usetech.dvente.requests.users.ForgotPasswordRequest;
import com.usetech.dvente.requests.users.ResetPasswordRequest;
import com.usetech.dvente.services.users.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/change")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request
    ) {
        try {
            passwordService.changePassword(user, request);
            return ResponseEntity.ok().body("Mot de passe mis à jour avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur interne");
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            passwordService.sendResetCode(request);
            return ResponseEntity.ok().body("Code envoyé à l'adresse email");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur envoi code reset: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Erreur interne");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordService.resetPassword(request);
            return ResponseEntity.ok().body("Mot de passe réinitialisé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur reset mot de passe: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Erreur interne");
        }
    }
}