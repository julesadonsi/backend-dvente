package com.usetech.dvente.controllers.users;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.requests.ChangeEmailRequest;
import com.usetech.dvente.requests.users.UpdateUserProfileRequest;
import com.usetech.dvente.responses.products.PaginatedProductResponse;
import com.usetech.dvente.responses.users.AuthenticatedUserResponse;
import com.usetech.dvente.responses.users.UserResponse;
import com.usetech.dvente.services.auth.VerificationCodeService;
import com.usetech.dvente.services.notifs.EmailService;
import com.usetech.dvente.services.products.ProductLikeService;
import com.usetech.dvente.services.users.UserService;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.utils.ShopUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserService userService;
    private final ProductLikeService  productLikeService;
    private final ShopRepository shopRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final VerificationCodeService codeService;

    @Value("${app.url}")
    private String apiUrl;

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
                        UserResponse.fromUser(user), shop
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


    @GetMapping("/liked-products")
    @Operation(
            summary = "Get User Liked Products",
            description = "Récupérer tous les produits aimés par l'utilisateur"
    )
    public ResponseEntity<?> getUserLikedProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
            @AuthenticationPrincipal User user
    ) {
        try {
            PaginatedProductResponse likedProducts = productLikeService.getUserLikedProducts(
                    user, page, pageSize, apiUrl
            );

            return ResponseEntity.ok(likedProducts);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }


    @PostMapping("/change/email/code")
    public ResponseEntity<Map<String, Object>> changeEmailCode(
            @RequestParam String newEmail, Authentication authentication
    ) {

        Map<String, Object> response = new HashMap<>();

        if (newEmail == null || newEmail.isBlank()) {
            response.put("status", "error");
            response.put("message", "L'email ne peut pas être vide");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (!newEmail.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
            response.put("status", "error");
            response.put("message", "L'email n'est pas valide");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.findByEmail(newEmail).isPresent()) {
            response.put("status", "error");
            response.put("message", "L'email est déjà utilisé");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        String code = ShopUtils.generate6DigitCode();
        codeService.saveEmailCode(newEmail, code);
        emailService.sendUpdateEmailVerificationCode(newEmail, code);

        response.put("status", "success");
        response.put("message", "Email de vérification envoyé");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/change/email")
    public ResponseEntity<?> verifyEmail(
            @RequestParam String email, @RequestParam String code, Authentication authentication
    ) {
        String savedCode = codeService.getEmailCode(email);

        if (savedCode == null || !savedCode.equals(code)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Code invalide ou expiré"
            ));
        }
        User currentUser = (User) authentication.getPrincipal();
        userRepository.updateUserEmail(currentUser.getId(), email);
        codeService.removeEmailCode(email);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email mis à jour avec succès"
        ));
    }




}