package com.usetech.dvente.responses.shops;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.usetech.dvente.configs.ApiConfig;
import com.usetech.dvente.entities.users.ShopGallery;
import com.usetech.dvente.responses.users.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class ShopGalleryResponse {

    private static ApiConfig apiConfig;

    @Autowired
    public void setApiConfig(ApiConfig apiConfig) {
        ShopGalleryResponse.apiConfig = apiConfig;
    }

    private UUID id;
    private String image;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public static ShopGalleryResponse fromEntity(ShopGallery gallery) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return ShopGalleryResponse.builder()
                .id(gallery.getId())
                .image(gallery.getImage() != null ? apiConfig.getApiUrl() + gallery.getImage() : null)
                .createdAt(gallery.getCreatedAt() != null ? gallery.getCreatedAt().format(formatter) : null)
                .updatedAt(gallery.getUpdatedAt() != null ? gallery.getUpdatedAt().format(formatter) : null)
                .build();
    }
}
