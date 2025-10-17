package com.usetech.dvente.services.auth;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserService {

    private final UserRepository userRepository;

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
        user.setAvatar(picture);
        return userRepository.save(user);
    }

    private User registerNewUser(String email, String name, String picture, String googleId) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setAvatar(picture);
        newUser.setGoogleId(googleId);
        newUser.setEmailConfirmed(true);
        newUser.setProvider("GOOGLE");

        return userRepository.save(newUser);
    }
}
