package com.usetech.dvente.responses.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopGallery;
import com.usetech.dvente.entities.users.ShopUrlHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {

    private UUID id;

    @JsonProperty("shop_url")
    private String shopUrl;

    private String email;

    @JsonProperty("whatsapp_number")
    private String whatsappNumber;

    private String address;

    private String city;

    private String country;

    private String description;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("numero_ifu")
    private String numeroIfu;

    @JsonProperty("domaine_activity")
    private String domaineActivity;

    @JsonProperty("regime_fiscale")
    private String regimeFiscale;

    @JsonProperty("num_rcm")
    private String numRcm;

    private String status;

    private Boolean visible;

    private String logo;

    @JsonProperty("can_change_shop_url")
    private Boolean canChangeShopUrl;

    private List<ShopGalleryResponse> gallery;

    @JsonProperty("ifu_document")
    private String ifuDocument;

    @JsonProperty("rcm_document")
    private String rcmDocument;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public static ShopResponse fromEntity(Shop shop, String apiUrl) {
        return fromEntity(shop, apiUrl, null, null);
    }

    public static ShopResponse fromEntity(
            Shop shop,
            String apiUrl,
            List<ShopGallery> galleries,
            ShopUrlHistory lastUrlChange
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return ShopResponse.builder()
                .id(shop.getId())
                .shopUrl(shop.getShopUrl())
                .email(shop.getEmail())
                .whatsappNumber(shop.getWhatsappNumber())
                .address(shop.getAddress())
                .city(shop.getCity())
                .country(shop.getCountry())
                .description(shop.getDescription())
                .shopName(shop.getShopName())
                .numeroIfu(shop.getNumeroIfu())
                .domaineActivity(shop.getDomaineActivity())
                .regimeFiscale(shop.getRegimeFiscale())
                .numRcm(shop.getNumRcm())
                .status(shop.getStatus() != null ? shop.getStatus().name() : null)
                .visible(shop.isVisible())
                // Champs calculés
                .logo(buildLogoUrl(shop, apiUrl))
                .canChangeShopUrl(calculateCanChangeShopUrl(lastUrlChange))
                .gallery(buildGallery(galleries, apiUrl))
                .ifuDocument(buildDocumentUrl(shop.getIfuDocument(), apiUrl))
                .rcmDocument(buildDocumentUrl(shop.getRcmDocument(), apiUrl))
                .createdAt(shop.getCreatedAt() != null ? shop.getCreatedAt().format(formatter) : null)
                .updatedAt(shop.getUpdatedAt() != null ? shop.getUpdatedAt().format(formatter) : null)
                .build();
    }

    private static String buildLogoUrl(Shop shop, String apiUrl) {
        if (shop.getLogo() != null && !shop.getLogo().isEmpty()) {
            return apiUrl + shop.getLogo();
        }

        String shopName = shop.getNameOfShop() != null ? shop.getNameOfShop() : "Shop";
        return String.format(
                "https://ui-avatars.com/api/?background=0D8ABC&color=fff&name=%s",
                shopName.replace(" ", "+")
        );
    }

    private static Boolean calculateCanChangeShopUrl(ShopUrlHistory lastChange) {
        if (lastChange == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        long daysSinceLastChange = ChronoUnit.DAYS.between(lastChange.getChangedAt(), now);
        return daysSinceLastChange >= 30;
    }

    private static List<ShopGalleryResponse> buildGallery(List<ShopGallery> galleries, String apiUrl) {
        if (galleries == null || galleries.isEmpty()) {
            return List.of();
        }

        return galleries.stream()
                .map(gallery -> ShopGalleryResponse.fromEntity(gallery, apiUrl))
                .collect(Collectors.toList());
    }

    private static String buildDocumentUrl(String document, String apiUrl) {
        if (document != null && !document.isEmpty()) {
            return apiUrl + document;
        }
        return null;
    }

    public static ShopResponse fromShop(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .shopUrl(shop.getShopUrl())
                .email(shop.getEmail())
                .address(shop.getAddress())
                .whatsappNumber(shop.getWhatsappNumber())
                .description(shop.getDescription())
                .numeroIfu(shop.getNumeroIfu())
                .numRcm(shop.getNumRcm())
                .city(shop.getCity())
                .country(shop.getCountry())
                .domaineActivity(shop.getDomaineActivity())
                .regimeFiscale(shop.getRegimeFiscale())
                .ifuDocument(shop.getIfuDocument())
                .rcmDocument(shop.getRcmDocument())
                .createdAt(String.valueOf(shop.getCreatedAt()))
                .updatedAt(String.valueOf(shop.getUpdatedAt()))
                .build();
    }
}