package com.usetech.dvente.services;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.entities.users.UserRole;
import com.usetech.dvente.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.email:admin@dvente.com}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        createDefaultAdminIfNotExists();
    }

    private void createDefaultAdminIfNotExists() {
        boolean adminExists = userRepository.existsByRoleAndIsActiveTrue(UserRole.ADMIN);

        if (!adminExists) {
            User admin = User.builder()
                    .name("Administrateur")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .isStaff(true)
                    .emailConfirmed(true)
                    .build();

            userRepository.save(admin);
            log.info("Admin par défaut créé avec l'email: {}", adminEmail);
        } else {
            log.info("Un administrateur existe déjà dans la base de données");
        }
    }
}