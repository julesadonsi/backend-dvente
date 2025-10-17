package com.usetech.dvente.responses.products;

import lombok.*;

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
    private Boolean hasPrevious;
}