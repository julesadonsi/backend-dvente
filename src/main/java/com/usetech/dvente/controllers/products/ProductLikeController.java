package com.usetech.dvente.controllers.products;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.services.products.ProductLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Product Likes", description = "API pour la gestion des likes de produits")
public class ProductLikeController {

    private final ProductLikeService productLikeService;

    @PostMapping("/{productId}/like")
    @Operation(summary = "Like Product", description = "Aimer un produit")
    public ResponseEntity<?> likeProduct(
            @PathVariable UUID productId,
            @AuthenticationPrincipal User user
    ) {
        try {
            boolean isLiked = productLikeService.toggleLike(productId, user);

            return ResponseEntity.ok(Map.of(
                    "liked", isLiked,
                    "message", isLiked ? "Produit aimé" : "Like retiré"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}/like")
    @Operation(summary = "Unlike Product", description = "Retirer le like d'un produit")
    public ResponseEntity<?> unlikeProduct(
            @PathVariable UUID productId,
            @AuthenticationPrincipal User user
    ) {
        try {
            productLikeService.removeLike(productId, user);

            return ResponseEntity.ok(Map.of(
                    "liked", false,
                    "message", "Like retiré"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}