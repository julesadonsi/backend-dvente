package com.usetech.dvente.requests.shops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopGalleryRequest {

    private List<MultipartFile> galleryImages;
    private List<UUID> deleteImages;
}
