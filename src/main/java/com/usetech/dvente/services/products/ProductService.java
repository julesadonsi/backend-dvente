package com.usetech.dvente.services.products;

import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductImage;
import com.usetech.dvente.repositories.products.ProductRepository;
import com.usetech.dvente.responses.products.CategoryResponse;
import com.usetech.dvente.responses.products.PaginatedProductResponse;
import com.usetech.dvente.responses.products.ProductResponse;
import com.usetech.dvente.responses.shops.ShopResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Récupère la liste paginée des produits
     */
    @Transactional(readOnly = true)
    public PaginatedProductResponse getProducts(
            Integer page,
            Integer pageSize,
            UUID categoryId,
            HttpServletRequest request
    ) {
        // Validation et normalisation des paramètres
        int validPage = Math.max(page != null ? page : 1, 1) - 1; // Spring Data commence à 0
        int validPageSize = Math.min(
                Math.max(pageSize != null ? pageSize : DEFAULT_PAGE_SIZE, 1),
                MAX_PAGE_SIZE
        );

        // Créer le Pageable
        Pageable pageable = PageRequest.of(validPage, validPageSize);

        // Récupérer les produits
        Page<Product> productPage;
        if (categoryId != null) {
            productPage = productRepository.findActiveProductsByCategory(categoryId, pageable);
        } else {
            productPage = productRepository.findAllActiveProducts(pageable);
        }

        // Convertir en DTO
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> convertToResponse(product, request))
                .collect(Collectors.toList());

        // Construire la réponse paginée
        return PaginatedProductResponse.builder()
                .data(productResponses)
                .message("success")
                .total(productPage.getTotalElements())
                .page(validPage + 1) // Retourner page en base 1
                .pageSize(validPageSize)
                .totalPages(productPage.getTotalPages())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }

    /**
     * Convertit une entité Product en ProductResponse
     */
    private ProductResponse convertToResponse(Product product, HttpServletRequest request) {
        // Construire l'URL de base pour les images
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        // Récupérer le nombre d'avis
        Long reviewCount = productRepository.countReviewsByProductId(product.getId());

        // Convertir les autres images
        List<String> otherImages = product.getOtherImages().stream()
                .map(img -> buildImageUrl(baseUrl, img.getImage()))
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .primaryImage(buildImageUrl(baseUrl, product.getPrimaryImage()))
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .category(CategoryResponse.fromCategory(product.getCategory()))
                .shopAuthor(ShopResponse.fromShop(product.getShopAuthor()))
                .otherImages(otherImages)
                .reviewCount(reviewCount)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Construit l'URL complète d'une image
     */
    private String buildImageUrl(String baseUrl, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // Si l'URL commence déjà par http/https, la retourner telle quelle
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }

        // Sinon, construire l'URL complète
        return baseUrl + (imagePath.startsWith("/") ? "" : "/") + imagePath;
    }
}