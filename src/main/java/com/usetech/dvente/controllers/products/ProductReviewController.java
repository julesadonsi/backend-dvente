
package com.usetech.dvente.controllers.products;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.requests.products.CreateReviewRequest;
import com.usetech.dvente.responses.products.PaginatedReviewResponse;
import com.usetech.dvente.responses.products.ProductReviewResponse;
import com.usetech.dvente.services.products.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Product Reviews", description = "API pour la gestion des avis produits")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @GetMapping("/{productId}/reviews")
    @Operation(summary = "Get Product Reviews", description = "Récupérer les avis d'un produit")
    public ResponseEntity<?> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize
    ) {
        try {
            PaginatedReviewResponse reviews = productReviewService.getProductReviews(
                    productId, page, pageSize
            );

            return ResponseEntity.ok(reviews);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/{productId}/reviews")
    @Operation(summary = "Create Product Review", description = "Créer un avis pour un produit")
    public ResponseEntity<?> createProductReview(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            ProductReviewResponse review = productReviewService.createReview(
                    productId, request, user
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(review);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}