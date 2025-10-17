package com.usetech.dvente.controllers.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.requests.shops.CreateMerchantRequest;
import com.usetech.dvente.responses.shops.CreateMerchantResponse;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.responses.users.UserResponse;
import com.usetech.dvente.services.shops.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants")
@Tag(name = "Marchand", description = "API pour la gestion des marchands")
public class CreateMerchantController {

    private final ShopService shopService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create Merchand Request",
            description = "Créer une demande de compte marchand"
    )
    public ResponseEntity<?> createMerchant(
            @Valid @ModelAttribute CreateMerchantRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("detail", "Authentication requise");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User user = (User) authentication.getPrincipal();

        // Vérifier si l'URL de la boutique existe déjà
        if (shopService.existsByShopUrl(request.getShopUrl())) {
            Map<String, String> error = new HashMap<>();
            error.put("detail", "Nous ne pouvons pas procédé à la création de votre compte avec ses information");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // Vérifier si l'utilisateur a déjà une boutique
        if (shopService.userHasShop(user.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("detail", "Nous ne pouvons pas procédé à la création de votre compte avec ses information");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            Shop merchant = shopService.createMerchant(request, user);
            shopService.sendMerchantAccountCreateEmail(merchant.getId());
            CreateMerchantResponse response = CreateMerchantResponse.builder()
                    .marchand(ShopResponse.fromShop(merchant))
                    .user(UserResponse.fromUser(user))
                    .authenticated(true)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("detail", "Une erreur est survenue lors de la création du compte marchand");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}