package com.usetech.dvente.responses.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductImage;
import com.usetech.dvente.responses.shops.ShopResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;

    @JsonProperty("discount_price")
    private BigDecimal discountPrice;

    @JsonProperty("primary_image")
    private String primaryImage;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @JsonProperty("is_active")
    private Boolean isActive;

    private CategoryResponse category;

    @JsonProperty("shop_author")
    private ShopResponse shopAuthor;

    @JsonProperty("other_images")
    @Builder.Default
    private List<String> otherImages = new ArrayList<>();

    @JsonProperty("review_count")
    private Long reviewCount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static ProductResponse fromEntity(Product product, String apiUrl, Long reviewCount) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .primaryImage(buildImageUrl(product.getPrimaryImage(), apiUrl))
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .category(CategoryResponse.fromCategory(product.getCategory()))
                .shopAuthor(ShopResponse.fromEntity(product.getShopAuthor()))
                .otherImages(buildOtherImagesUrls(product.getOtherImages(), apiUrl))
                .reviewCount(reviewCount)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductResponse fromEntity(Product product, String apiUrl) {
        return fromEntity(product, apiUrl, 0L);
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
}