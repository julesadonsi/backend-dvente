package com.usetech.dvente.controllers.products;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.responses.products.ProductImageUploadResponse;
import com.usetech.dvente.services.products.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API pour la gestion des produits")
public class ProductImageController {

    private final ProductImageService productImageService;
    private final ShopRepository shopRepository;

    @Value("${server.backend.url}")
    private String serverApiUrl;

    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SHOP')")
    @Operation(
            summary = "Upload Product Image",
            description = "Uploader une image pour un produit. Formats acceptés: JPG, JPEG, PNG, GIF, WEBP"
    )
    public ResponseEntity<?> uploadProductImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal User user
    ) {
        try {
            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Vous devez être marchand pour uploader des images"));
            String imagePath = productImageService.saveProductImage(image);
            ProductImageUploadResponse response = ProductImageUploadResponse.success(imagePath, serverApiUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }


}