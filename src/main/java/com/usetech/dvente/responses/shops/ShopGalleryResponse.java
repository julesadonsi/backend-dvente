package com.usetech.dvente.responses.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usetech.dvente.entities.users.ShopGallery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopGalleryResponse {

    private UUID id;
    private String image;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public static ShopGalleryResponse fromEntity(ShopGallery gallery, String apiUrl) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return ShopGalleryResponse.builder()
                .id(gallery.getId())
                .image(gallery.getImage() != null ? apiUrl + gallery.getImage() : null)
                .createdAt(gallery.getCreatedAt() != null ? gallery.getCreatedAt().format(formatter) : null)
                .updatedAt(gallery.getUpdatedAt() != null ? gallery.getUpdatedAt().format(formatter) : null)
                .build();
    }
}
