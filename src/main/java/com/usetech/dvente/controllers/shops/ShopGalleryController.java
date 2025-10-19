package com.usetech.dvente.controllers.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopGallery;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.shops.ShopGalleryRepository;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.requests.shops.ShopDocumentsRequest;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.responses.users.AuthenticatedUserResponse;
import com.usetech.dvente.responses.users.UserResponse;
import com.usetech.dvente.services.FileStorageService;
import com.usetech.dvente.services.shops.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
@Tag(name = "Marchand", description = "API pour la gestion des marchands")
public class ShopGalleryController {

    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final ShopGalleryRepository shopGalleryRepository;
    private final FileStorageService fileStorageService;

    @Value("${server.backend.url}")
    private String serverApiUrl;

    @PatchMapping(value = "/update/galleries", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMerchantGalleries(
            @RequestParam(value = "gallery_images", required = false) List<MultipartFile> galleryImages,
            @RequestParam(value = "delete_images", required = false) List<UUID> deleteImages,
            @AuthenticationPrincipal User user
    ) {
        try {
            Shop merchant = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("No merchant found for this user"));
            shopService.updateShopGalleries(merchant, galleryImages, deleteImages);

            Shop updatedMerchant = shopRepository.findById(merchant.getId())
                    .orElse(merchant);

            List<ShopGallery> galleries = shopGalleryRepository
                    .findByShopIdOrderByCreatedAtDesc(updatedMerchant.getId());

            updatedMerchant.setGallery(galleries);

            System.out.println("api " + serverApiUrl);

            ShopResponse shopResponse = ShopResponse.fromEntity(updatedMerchant, galleries, null);

            AuthenticatedUserResponse response = AuthenticatedUserResponse.builder()
                    .authenticated(true)
                    .user(UserResponse.fromUser(user))
                    .shops(shopResponse)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("detail", "An error occurred while updating galleries"));
        }
    }


    @PatchMapping(value = "/update/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update Shop Documents",
            description = "Met à jour les documents IFU et/ou RCM d'un marchand. Formats acceptés: PDF, DOC, DOCX"
    )
    public ResponseEntity<?> updateShopDocuments(
            @Valid @ModelAttribute ShopDocumentsRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            if ((request.getIfuDocument() == null || request.getIfuDocument().isEmpty()) &&
                    (request.getRcmDocument() == null || request.getRcmDocument().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Aucun document fourni"));
            }

            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            Shop updatedShop = shopService.updateShopDocuments(
                    shop,
                    request.getIfuDocument(),
                    request.getRcmDocument()
            );

            AuthenticatedUserResponse response = AuthenticatedUserResponse.authenticated(
                    UserResponse.fromUser(user),
                    updatedShop
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

}
