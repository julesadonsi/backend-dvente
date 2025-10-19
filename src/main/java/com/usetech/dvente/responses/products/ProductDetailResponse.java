package com.usetech.dvente.responses.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usetech.dvente.entities.products.Keyword;
import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductImage;
import com.usetech.dvente.responses.shops.ShopResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private UUID id;

    private CategoryResponse category;

    @JsonProperty("primary_image")
    private String primaryImage;

    @JsonProperty("shop_author")
    private ShopResponse shopAuthor;

    @JsonProperty("other_images")
    private List<String> otherImages;

    private String title;

    @JsonProperty("short_description")
    private String shortDescription;

    private String description;

    private String state;

    private BigDecimal price;

    @JsonProperty("price_promo")
    private BigDecimal pricePromo;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_promo")
    private Boolean isPromo;

    @JsonProperty("like_count")
    private Long likeCount;

    @JsonProperty("views_count")
    private Long viewsCount;

    @JsonProperty("stock_count")
    private Integer stockCount;

    @JsonProperty("in_stock")
    private Boolean inStock;

    private String slug;

    private List<KeywordResponse> keywords;

    @JsonProperty("has_discount")
    private Boolean hasDiscount;

    private BigDecimal discount;

    @JsonProperty("review_count")
    private Long reviewCount;

    public static ProductDetailResponse fromEntity(Product product, String apiUrl, Long reviewCount) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .shortDescription(extractShortDescription(product.getDescription()))
                .price(product.getPrice())
                .pricePromo(product.getDiscountPrice())
                .primaryImage(buildImageUrl(product.getPrimaryImage(), apiUrl))
                .otherImages(buildOtherImagesUrls(product.getOtherImages(), apiUrl))
                .stockCount(product.getStockQuantity())
                .inStock(product.getStockQuantity() != null && product.getStockQuantity() > 0)
                .isActive(product.getIsActive())
                .isPromo(product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                .hasDiscount(product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(product.getPrice()) < 0)
                .discount(calculateDiscount(product.getPrice(), product.getDiscountPrice()))
                .category(CategoryResponse.fromCategory(product.getCategory()))
                .shopAuthor(ShopResponse.fromEntity(product.getShopAuthor()))
                .keywords(buildKeywords(product.getKeywords()))
                .reviewCount(reviewCount)
                .likeCount(0L) // TODO: Implement likes functionality
                .viewsCount(0L) // TODO: Implement views count
                .slug(generateSlug(product.getName()))
                .state("new") // TODO: Add state field to Product entity if needed
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private static String buildImageUrl(String imagePath, String apiUrl) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return imagePath.startsWith("http") ? imagePath : apiUrl + imagePath;
    }

    private static List<String> buildOtherImagesUrls(List<ProductImage> images, String apiUrl) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(img -> buildImageUrl(img.getImage(), apiUrl))
                .collect(Collectors.toList());
    }

    private static List<KeywordResponse> buildKeywords(List<Keyword> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }
        return keywords.stream()
                .map(KeywordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private static BigDecimal calculateDiscount(BigDecimal price, BigDecimal discountPrice) {
        if (price == null || discountPrice == null || discountPrice.compareTo(price) >= 0) {
            return BigDecimal.ZERO;
        }
        return price.subtract(discountPrice)
                .divide(price, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private static String extractShortDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "";
        }
        return description.length() > 150 ? description.substring(0, 150) + "..." : description;
    }

    private static String generateSlug(String name) {
        if (name == null) {
            return "";
        }
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}