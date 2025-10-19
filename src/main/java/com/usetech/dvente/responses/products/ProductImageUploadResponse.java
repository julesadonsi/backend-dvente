package com.usetech.dvente.responses.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUploadResponse {

    private String path;
    private String fullUrl;
    private String message;

    public static ProductImageUploadResponse success(String path, String apiUrl) {
        String fullUrl = path.startsWith("http") ? path : apiUrl + path;
        return ProductImageUploadResponse.builder()
                .path(path)
                .fullUrl(fullUrl)
                .message("Image uploadée avec succès")
                .build();
    }
}