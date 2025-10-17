package com.usetech.dvente.services.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopGallery;
import com.usetech.dvente.entities.users.ShopUrlHistory;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.shops.ShopGalleryRepository;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.repositories.shops.ShopUrlHistoryRepository;
import com.usetech.dvente.requests.shops.CreateMerchantRequest;
import com.usetech.dvente.responses.shops.ShopResponse;
import com.usetech.dvente.services.FileStorageService;
import com.usetech.dvente.services.notifs.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // Charger les galleries
        List<ShopGallery> galleries = shopGalleryRepository.findByShop_IdOrderByCreatedAtDesc(shopId);

        // Charger le dernier changement d'URL
        ShopUrlHistory lastUrlChange = shopUrlHistoryRepository
                .findFirstByShop_IdOrderByChangedAtDesc(shopId)
                .orElse(null);

        return ShopResponse.fromEntity(shop, apiUrl, galleries, lastUrlChange);
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
                    return ShopResponse.fromEntity(shop, apiUrl, galleries, lastUrlChange);
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
    public Shop createMerchant(CreateMerchantRequest request, User user) {
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
}