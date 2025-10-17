package com.usetech.dvente.responses.products;

import com.usetech.dvente.responses.shops.ShopResponse;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String primaryImage;
    private Integer stockQuantity;
    private Boolean isActive;

    private CategoryResponse category;
    private ShopResponse shopAuthor;

    @Builder.Default
    private List<String> otherImages = new ArrayList<>();

    private Long reviewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}