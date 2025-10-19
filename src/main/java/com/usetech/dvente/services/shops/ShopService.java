package com.usetech.dvente.services.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopGallery;
import com.usetech.dvente.entities.users.ShopUrlHistory;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.shops.ShopGalleryRepository;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.repositories.shops.ShopUrlHistoryRepository;
import com.usetech.dvente.requests.shops.CreateShopRequest;
import com.usetech.dvente.requests.shops.UpdateShopRequest;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.services.FileStorageService;
import com.usetech.dvente.services.notifs.EmailService;
import com.usetech.dvente.utils.ShopUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopGalleryRepository shopGalleryRepository;
    private final ShopUrlHistoryRepository shopUrlHistoryRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    @Value("${app.url}")
    private String apiUrl;

    @Value("${app.name:DVente}")
    private String appName;

    @Value("${app.url:http://localhost:4200}")
    private String appUrl;

    @Value("${app.support.email:support@dvente.com}")
    private String supportEmail;

    @Value("${app.support.whatsapp:+229 XX XX XX XX}")
    private String supportWhatsapp;

    @Transactional(readOnly = true)
    public ShopResponse getShopById(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        List<ShopGallery> galleries = shopGalleryRepository.findByShop_IdOrderByCreatedAtDesc(shopId);
        ShopUrlHistory lastUrlChange = shopUrlHistoryRepository
                .findFirstByShop_IdOrderByChangedAtDesc(shopId)
                .orElse(null);
        return ShopResponse.fromEntity(shop, galleries, lastUrlChange);
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> getAllShops() {
        List<Shop> shops = shopRepository.findAll();

        return shops.stream()
                .map(shop -> {
                    List<ShopGallery> galleries = shopGalleryRepository.findByShopOrderByCreatedAtDesc(shop);
                    ShopUrlHistory lastUrlChange = shopUrlHistoryRepository
                            .findFirstByShopOrderByChangedAtDesc(shop)
                            .orElse(null);
                    return ShopResponse.fromEntity(shop, galleries, lastUrlChange);
                })
                .toList();
    }

    public boolean existsByShopUrl(String shopUrl) {
        return shopRepository.existsByShopUrl(shopUrl);
    }

    public boolean userHasShop(UUID userId) {
        return shopRepository.existsByUserId(userId);
    }

    @Transactional
    public Shop createMerchant(CreateShopRequest request, User user) {
        String ifuDocumentPath = fileStorageService.saveDocument(request.getIfuDocument(), "ifu");
        String rcmDocumentPath = fileStorageService.saveDocument(request.getRcmDocument(), "rcm");

        Shop shop = Shop.builder()
                .shopName(request.getShopName())
                .shopUrl(request.getShopUrl())
                .email(request.getEmail())
                .address(request.getAddress())
                .whatsappNumber(request.getWhatsappNumber())
                .description(request.getDescription())
                .numeroIfu(request.getNumeroIfu())
                .numRcm(request.getNumRcm())
                .city(request.getCity())
                .country(request.getCountry())
                .domaineActivity(request.getDomaineActivity())
                .regimeFiscale(request.getRegimeFiscale())
                .ifuDocument(ifuDocumentPath)
                .rcmDocument(rcmDocumentPath)
                .user(user)
                .build();

        return shopRepository.save(shop);
    }

    @Async
    public void sendMerchantAccountCreateEmail(UUID merchantId) {
        Shop shop = shopRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("appName", appName);
        variables.put("merchantName", shop.getUser().getName());
        variables.put("shopName", shop.getShopName());
        variables.put("shopUrl", shop.getShopUrl());
        variables.put("email", shop.getEmail());
        variables.put("city", shop.getCity());
        variables.put("country", shop.getCountry());
        variables.put("domaineActivity", shop.getDomaineActivity());
        variables.put("dashboardUrl", appUrl + "/dashboard");
        variables.put("appUrl", appUrl);
        variables.put("supportEmail", supportEmail);
        variables.put("whatsappNumber", shop.getWhatsappNumber());
        variables.put("termsUrl", appUrl + "/terms");
        variables.put("privacyUrl", appUrl + "/privacy");

        emailService.sendHtmlEmail(
                shop.getEmail(),
                "Demande de compte marchand reçue - " + appName,
                "emails/merchantAccountCreated",
                variables
        );
    }

    public Shop updateShop(UpdateShopRequest request, Shop shop) {
        if (request.getShopName() != null && !request.getShopName().isBlank()) {
            shop.setShopName(request.getShopName());
        }

        if (request.getDescription() != null) {
            shop.setDescription(request.getDescription());
        }

        if (request.getShopUrl() != null && !request.getShopUrl().isBlank()) {
            shop.setShopUrl(request.getShopUrl());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            shop.setEmail(request.getEmail());
        }

        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            shop.setAddress(request.getAddress());
        }

        if (request.getWhatsappNumber() != null && !request.getWhatsappNumber().isBlank()) {
            shop.setWhatsappNumber(request.getWhatsappNumber());
        }

        if (request.getNumeroIfu() != null && !request.getNumeroIfu().isBlank()) {
            shop.setNumeroIfu(request.getNumeroIfu());
        }

        if (request.getNumRcm() != null && !request.getNumRcm().isBlank()) {
            shop.setNumRcm(request.getNumRcm());
        }

        if (request.getCity() != null && !request.getCity().isBlank()) {
            shop.setCity(request.getCity());
        }

        if (request.getCountry() != null && !request.getCountry().isBlank()) {
            shop.setCountry(request.getCountry());
        }

        if (request.getDomaineActivity() != null && !request.getDomaineActivity().isBlank()) {
            shop.setDomaineActivity(request.getDomaineActivity());
        }

        if (request.getRegimeFiscale() != null && !request.getRegimeFiscale().isBlank()) {
            shop.setRegimeFiscale(request.getRegimeFiscale());
        }

        shop.setUpdatedAt(LocalDateTime.now());
        return shopRepository.save(shop);
    }

    public void updateShopGalleries(Shop shop, List<MultipartFile> newImages, List<UUID> imagesToDelete) {
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                String imagePath = fileStorageService.saveDocument(image, "shop-galleries");

                ShopGallery gallery = ShopGallery.builder()
                        .shop(shop)
                        .image(imagePath)
                        .build();

                shopGalleryRepository.save(gallery);
            }
        }
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            List<ShopGallery> galleriesToDelete = shopGalleryRepository.findAllById(imagesToDelete);
            for (ShopGallery gallery : galleriesToDelete) {
                if (gallery.getImage() != null && !gallery.getImage().isEmpty()) {
                    fileStorageService.deleteDocument(gallery.getImage());
                }
            }
            shopGalleryRepository.deleteAll(galleriesToDelete);
        }

        shopRepository.findById(shop.getId());
    }


    public Shop updateShopDocuments(Shop shop, MultipartFile ifuDocument, MultipartFile rcmDocument) {
        boolean hasUpdates = false;

        if (ifuDocument != null && !ifuDocument.isEmpty()) {
            if (shop.getIfuDocument() != null && !shop.getIfuDocument().isEmpty()) {
                fileStorageService.deleteDocument(shop.getIfuDocument());
            }

            String ifuPath = fileStorageService.saveDocument(ifuDocument, "ifu");
            shop.setIfuDocument(ifuPath);
            hasUpdates = true;
        }
        if (rcmDocument != null && !rcmDocument.isEmpty()) {
            if (shop.getRcmDocument() != null && !shop.getRcmDocument().isEmpty()) {
                fileStorageService.deleteDocument(shop.getRcmDocument());
            }
            String rcmPath = fileStorageService.saveDocument(rcmDocument, "rcm");
            shop.setRcmDocument(rcmPath);
            hasUpdates = true;
        }

        if (!hasUpdates) {
            throw new IllegalArgumentException("Aucun document fourni");
        }

        shop.setUpdatedAt(java.time.LocalDateTime.now());
        return shopRepository.save(shop);
    }

    @Transactional(readOnly = true)
    public Shop getShopByShopUrl(String shopUrl) {
        String cleanedUrl = ShopUtils.removeAtSymbol(shopUrl);
        return shopRepository.findByShopUrl(cleanedUrl)
                .orElse(null);
    }

}