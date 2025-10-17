package com.usetech.dvente.controllers.products;

import com.usetech.dvente.responses.products.PaginatedProductResponse;
import com.usetech.dvente.services.products.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API pour la gestion des produits")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Liste des produits",
            description = "Récupère la liste paginée des produits actifs avec filtrage par catégorie optionnel"
    )
    public ResponseEntity<?> getProducts(
            @Parameter(description = "Numéro de la page (commence à 1)")
            @RequestParam(required = false) Integer page,

            @Parameter(description = "Nombre d'éléments par page (max 100)")
            @RequestParam(name = "page_size", required = false) Integer pageSize,

            @Parameter(description = "ID de la catégorie pour filtrer les produits")
            @RequestParam(name = "cat", required = false) UUID categoryId,

            HttpServletRequest request
    ) {
        try {
            PaginatedProductResponse response = productService.getProducts(
                    page,
                    pageSize,
                    categoryId,
                    request
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Une erreur est survenue lors de la récupération des produits: " + e.getMessage());
            errorResponse.put("data", null);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}