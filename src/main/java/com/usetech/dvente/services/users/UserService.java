package com.usetech.dvente.services.users;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.entities.users.UserRole;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.requests.users.UpdateUserProfileRequest;
import com.usetech.dvente.services.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final FileStorageService fileStorageService;

    public User register(String name, String email, String password) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .role(UserRole.CLIENT)
                .build();
        return userRepository.save(user);
    }


    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }


    public User getUserByEmail(String email) {
        return userRepository.
                findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    }

    /**
     * Updates the user profile with the given request.
     *
     * @param user the user to be updated
     * @param request the update request containing new user information
     * @return the updated user
     * @throws RuntimeException if the email or phone already exists
     */
    @Transactional
    public User updateUserProfile(User user, UpdateUserProfileRequest request) {
        if(request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName());
        }

        if(request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
            user.setEmailConfirmed(false);
        }

        if(request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (!request.getPhone().equals(user.getPhone())) {
                Optional<User> existingUser = userRepository.findByPhone(request.getPhone());
                if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                    throw new RuntimeException("Phone number already exists");
                }
                user.setPhone(request.getPhone());
                user.setPhoneConfirmed(false);
            }
        }

        if (request.getCountry() != null && !request.getCountry().trim().isEmpty()) {
            user.setCountry(request.getCountry());
        }

        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            user.setCity(request.getCity());
        }

        if(request.getAvatar() != null
                && !request.getAvatar().isEmpty()
                && request.getAvatar().getSize() > 0) {
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                fileStorageService.deleteAvatar(user.getAvatar());
            }
            String avatarUrl = fileStorageService.saveAvatar(request.getAvatar());
            user.setAvatar(avatarUrl);
        }
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
