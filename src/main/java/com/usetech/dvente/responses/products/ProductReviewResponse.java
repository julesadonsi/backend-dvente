package com.usetech.dvente.responses.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usetech.dvente.entities.products.ProductReview;
import com.usetech.dvente.responses.users.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponse {

    private UUID id;

    private Integer rating;

    private String comment;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private UserResponse user;

    public static ProductReviewResponse fromEntity(ProductReview review) {
        return ProductReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .user(UserResponse.fromUser(review.getUser()))
                .build();
    }
}