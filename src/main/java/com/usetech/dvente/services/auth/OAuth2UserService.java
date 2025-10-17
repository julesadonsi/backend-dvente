package com.usetech.dvente.services.auth;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public User processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String googleId = oAuth2User.getAttribute("sub");

        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name, picture, googleId))
                .orElseGet(() -> registerNewUser(email, name, picture, googleId));
    }

    private User updateExistingUser(User user, String name, String picture, String googleId) {
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
        }

        if (picture != null && !picture.isEmpty()) {
            String localAvatarPath = fileStorageService.saveAvatarFromUrl(picture, user.getId());
            if (localAvatarPath != null) {
                if (user.getAvatar() != null && user.getAvatar().startsWith("/uploads/")) {
                    fileStorageService.deleteAvatar(user.getAvatar());
                }
                user.setAvatar(localAvatarPath);
            } else {
                user.setAvatar(picture);
            }
        }

        return userRepository.save(user);
    }

    private User registerNewUser(String email, String name, String picture, String googleId) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setGoogleId(googleId);
        newUser.setEmailConfirmed(true);
        newUser.setProvider("GOOGLE");

        newUser = userRepository.save(newUser);

        if (picture != null && !picture.isEmpty()) {
            String localAvatarPath = fileStorageService.saveAvatarFromUrl(picture, newUser.getId());
            if (localAvatarPath != null) {
                newUser.setAvatar(localAvatarPath);
            } else {
                newUser.setAvatar(picture);
            }
            newUser = userRepository.save(newUser);
        }

        return newUser;
    }
}
