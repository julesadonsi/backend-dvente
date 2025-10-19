package com.usetech.dvente.controllers.products;

import com.usetech.dvente.responses.products.KeywordResponse;
import com.usetech.dvente.services.products.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API pour la gestion des produits")
public class ProductKeywordController {

    private final ProductService productService;

    @GetMapping("/keywords")
    @Operation(
            summary = "Get Product Keywords",
            description = "Récupérer tous les mots-clés de produits"
    )
    public ResponseEntity<?> getProductKeywords() {
        try {
            List<KeywordResponse> keywords = productService.getAllKeywords();
            return ResponseEntity.ok(keywords);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}
