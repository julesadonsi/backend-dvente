package com.usetech.dvente.controllers.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.requests.shops.CreateShopRequest;
import com.usetech.dvente.requests.shops.UpdateShopRequest;
import com.usetech.dvente.responses.products.PaginatedProductResponse;
import com.usetech.dvente.responses.shops.CreateMerchantResponse;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.responses.users.UserResponse;
import com.usetech.dvente.services.products.ProductService;
import com.usetech.dvente.services.shops.ShopService;
import com.usetech.dvente.utils.ShopUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
@Tag(name = "Marchand", description = "API pour la gestion des marchands")
public class ShopController {

    private final ShopService shopService;
    private final ShopRepository shopRepository;

    private final ProductService productService;


    @Value("${server.backend.url}")
    private String apiUrl;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create Merchand Request",
            description = "Créer une demande de compte marchand"
    )
    public ResponseEntity<?> createMerchant(
            @Valid @ModelAttribute CreateShopRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("detail", "Authentication requise");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User user = (User) authentication.getPrincipal();

        if (shopService.existsByShopUrl(request.getShopUrl())) {
            Map<String, String> error = new HashMap<>();
            error.put("detail", "Nous ne pouvons pas procédé à la création de votre compte avec ses information");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

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


    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getAvailableShops(
            @RequestParam("name") String name
    )
    {
        String url = ShopUtils.slugify(name);
        boolean exists = shopService.existsByShopUrl(url);

        Map<String, Object> response = new HashMap<>();
        if (exists) {
            response.put("error", "Shop exists");
        }
        response.put("available", exists);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> getShopRequestStatus(@AuthenticationPrincipal User user) {

        Optional<Shop> shopOpt = shopRepository.findFirstByUser(user);

        Map<String, Object> response = new HashMap<>();

        if (shopOpt.isEmpty()) {
            response.put("status", "NO_SHOP");
            response.put("shops", null);
            return ResponseEntity.ok().body(response);
        }

        Shop shop = shopOpt.get();
        response.put("status", shop.getStatus().name());
        response.put("shop", ShopResponse.fromShop(shop));

        return ResponseEntity.ok(response);
    }


    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateShop(
            @Valid @ModelAttribute UpdateShopRequest request,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("detail", "Authentication requise"));
        }

        User user = (User) authentication.getPrincipal();
        Shop shop = shopRepository.getShopByUser(user);

        if (shop == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("detail", "Aucun compte marchand trouvé pour cet utilisateur"));
        }

        try {
            Shop updatedShop = shopService.updateShop(request,shop);
            return ResponseEntity.ok(
                    Map.of(
                            "status", "UPDATED",
                            "shops", ShopResponse.fromShop(updatedShop),
                            "user", UserResponse.fromUser(user)
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("detail", "Erreur lors de la mise à jour du compte marchand"));
        }
    }


    @GetMapping("/{shopUrl}")
    public ResponseEntity<?> getMerchantByShopUrl(@PathVariable String shopUrl) {
        try {
            Shop shop = shopService.getShopByShopUrl(shopUrl);

            if (shop == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Boutique non trouvée"));
            }

            ShopResponse response = ShopResponse.fromEntity(shop);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }



    @GetMapping("/products")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
            summary = "Get Merchant Products",
            description = "Récupérer tous les produits d'un marchand avec pagination"
    )
    public ResponseEntity<?> getMerchantProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
            @AuthenticationPrincipal User user
    ) {
        try {
            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            if (page < 1) page = 1;
            if (pageSize < 1 || pageSize > 100) pageSize = 10;

            PaginatedProductResponse response = productService.getMerchantProducts(
                    shop, page, pageSize, apiUrl
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

}