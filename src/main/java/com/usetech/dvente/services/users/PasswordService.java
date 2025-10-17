package com.usetech.dvente.services.users;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.requests.users.ChangePasswordRequest;
import com.usetech.dvente.requests.users.ForgotPasswordRequest;
import com.usetech.dvente.requests.users.ResetPasswordRequest;
import com.usetech.dvente.services.notifs.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final Map<String, String> resetCodes = new HashMap<>();

    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void sendResetCode(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Aucun utilisateur trouvé avec cet email");
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        resetCodes.put(request.getEmail(), code);
        emailService.sendResetCode(request.getEmail(), code);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String savedCode = resetCodes.get(request.getEmail());
        if (savedCode == null || !savedCode.equals(request.getCode())) {
            throw new IllegalArgumentException("Code invalide ou expiré");
        }

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetCodes.remove(request.getEmail());
    }
}
