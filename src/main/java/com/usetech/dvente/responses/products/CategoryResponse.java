package com.usetech.dvente.responses.products;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CategoryResponse fromCategory(com.usetech.dvente.entities.products.Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImage())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
