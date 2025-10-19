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
public class PaginatedProductResponse {

    private List<ProductResponse> data;
    private String message;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Boolean hasNext;

    public static PaginatedProductResponse of(
            List<ProductResponse> products,
            Long total,
            Integer page,
            Integer pageSize,
            Integer totalPages,
            Boolean hasNext
    ) {
        return PaginatedProductResponse.builder()
                .data(products)
                .message("success")
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .build();
    }
}