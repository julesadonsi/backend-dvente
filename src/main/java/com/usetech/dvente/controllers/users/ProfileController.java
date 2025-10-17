package com.usetech.dvente.controllers.users;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.requests.users.UpdateUserProfileRequest;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.responses.users.AuthenticatedUserResponse;
import com.usetech.dvente.responses.users.UserResponse;
import com.usetech.dvente.services.users.UserService;
import com.usetech.dvente.repositories.shops.ShopRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final UserService userService;
    private final ShopRepository shopRepository;


    @Value("${app.url}")
    private String apiUrl;

    public ProfileController(UserService userService, ShopRepository shopRepository) {
        this.userService = userService;
        this.shopRepository = shopRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<AuthenticatedUserResponse>
    getAuthStatus(Authentication authentication) {
        if(authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal()))
        {
            return ResponseEntity.ok(AuthenticatedUserResponse.unauthenticated());
        }
        String email =  authentication.getName();
        User user = userService.getUserByEmail(email);
        Shop shop = shopRepository.findByUser(user).stream().findFirst().orElse(null);

        return  ResponseEntity.ok(
                AuthenticatedUserResponse.authenticated(
                        UserResponse.fromUser(user), shop, apiUrl
                )
        );
    }

    @PatchMapping(value = "profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @Valid @ModelAttribute UpdateUserProfileRequest request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            User updatedUser = userService.updateUserProfile(user, request);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


}