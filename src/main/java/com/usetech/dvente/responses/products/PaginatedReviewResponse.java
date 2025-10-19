package com.usetech.dvente.responses.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedReviewResponse {

    private List<ProductReviewResponse> data;
    private String message;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Boolean hasNext;
    private Double averageRating;

    public static PaginatedReviewResponse of(
            List<ProductReviewResponse> reviews,
            Long total,
            Integer page,
            Integer pageSize,
            Integer totalPages,
            Boolean hasNext,
            Double averageRating
    ) {
        return PaginatedReviewResponse.builder()
                .data(reviews)
                .message("success")
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .averageRating(averageRating)
                .build();
    }
}