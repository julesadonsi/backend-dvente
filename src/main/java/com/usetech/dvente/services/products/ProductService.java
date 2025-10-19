package com.usetech.dvente.services.products;

import com.usetech.dvente.entities.products.Keyword;
import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductImage;
import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.repositories.products.KeywordRepository;
import com.usetech.dvente.repositories.products.ProductRepository;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.responses.products.*;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.utils.ShopUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final KeywordRepository keywordRepository;
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

        return PaginatedProductResponse.builder()
                .data(productResponses)
                .message("success")
                .total(productPage.getTotalElements())
                .page(validPage + 1)
                .pageSize(validPageSize)
                .totalPages(productPage.getTotalPages())
                .hasNext(productPage.hasNext())
                .build();
    }

    /**
     * Convertit une entité Product en ProductResponse
     */
    private ProductResponse convertToResponse(Product product, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        Long reviewCount = productRepository.countReviewsByProductId(product.getId());

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
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        return baseUrl + (imagePath.startsWith("/") ? "" : "/") + imagePath;
    }


    /**
     * Retrieves the details of a product by its ID and API URL.
     * The method fetches the product information along with its review count,
     * and maps these details to a {@code ProductDetailResponse}.
     * The operation is executed in a read-only transactional context.
     *
     * @param productId the unique identifier of the product to retrieve details for
     * @param apiUrl the base API URL used for linking product resources
     * @return a {@code ProductDetailResponse} containing the product's details,
     *         or {@code null} if the product was not found
     */
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(UUID productId, String apiUrl) {
        Product product = productRepository.findProductWithDetails(productId)
                .orElse(null);
        if (product == null) {
            return null;
        }
        Long reviewCount = productRepository.countReviewsByProductId(productId);
        return ProductDetailResponse.fromEntity(product, apiUrl, reviewCount);
    }


    @Transactional(readOnly = true)
    public PaginatedProductResponse getMerchantProducts(Shop shop, int page, int pageSize, String apiUrl) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByShopAuthorWithDetails(shop, pageable);
        List<Product> products = productPage.getContent();
        Map<UUID, Long> reviewCounts = getReviewCountsForProducts(products);

        List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    Long reviewCount = reviewCounts.getOrDefault(product.getId(), 0L);
                    return ProductResponse.fromEntity(product, apiUrl, reviewCount);
                })
                .collect(Collectors.toList());

        return PaginatedProductResponse.of(
                productResponses,
                productPage.getTotalElements(),
                page,
                pageSize,
                productPage.getTotalPages(),
                productPage.hasNext()
        );
    }

    private Map<UUID, Long> getReviewCountsForProducts(List<Product> products) {
        if (products.isEmpty()) {
            return Map.of();
        }

        List<Object[]> reviewCounts = productRepository.countReviewsForProducts(products);

        return reviewCounts.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));
    }




    @Transactional(readOnly = true)
    public List<KeywordResponse> getAllKeywords() {
        List<Keyword> keywords = keywordRepository.findAll();
        return keywords.stream()
                .map(KeywordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaginatedProductResponse getProductsByShopUrl(
            String shopUrl, Integer page, Integer pageSize,
            String name, String apiUrl) {

        // Nettoyer l'URL (supprimer @ si présent)
        String cleanedUrl = ShopUtils.removeAtSymbol(shopUrl);

        Shop shop = shopRepository.findByShopUrl(cleanedUrl)
                .orElse(null);

        if (shop == null) {
            return null;
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Product> productPage;
        if (name != null && !name.trim().isEmpty()) {
            productPage = productRepository.findByShopAuthorAndNameContainingIgnoreCase(
                    shop, name, pageable
            );
        } else {
            productPage = productRepository.findByShopAuthor(shop, pageable);
        }

        // Mapper vers ProductResponse
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> ProductResponse.fromEntity(product, apiUrl))
                .collect(Collectors.toList());

        return PaginatedProductResponse.of(
                productResponses,
                productPage.getTotalElements(),
                page,
                pageSize,
                productPage.getTotalPages(),
                productPage.hasNext()
        );
    }

}