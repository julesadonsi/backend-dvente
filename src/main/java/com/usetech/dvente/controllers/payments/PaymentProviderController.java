package com.usetech.dvente.controllers.payments;

import com.usetech.dvente.entities.users.PaymentProvider;
import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.requests.payments.AddPaymentMethodRequest;
import com.usetech.dvente.responses.payments.ApiCollectResponse;
import com.usetech.dvente.responses.payments.PaymentMethodResponse;
import com.usetech.dvente.services.payments.PaymentProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops/payements")
@Tag(name = "Payment", description = "API pour la gestion des méthodes de paiement")
public class PaymentProviderController {

    private final PaymentProviderService paymentProviderService;
    private final ShopRepository shopRepository;

    @PostMapping("methods")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "Add payment method", description = "Ajouter une méthode de paiement pour le marchand")
    public ResponseEntity<?> addPaymentMethod(
            @Valid @RequestBody AddPaymentMethodRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            // Récupérer la boutique du marchand
            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            // Ajouter la méthode de paiement
            paymentProviderService.addPaymentMethod(shop, request);

            // Récupérer toutes les méthodes de paiement
            List<PaymentProvider> providers = paymentProviderService.getPaymentMethods(shop);
            List<PaymentMethodResponse> response = providers.stream()
                    .map(PaymentMethodResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiCollectResponse.success(response));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @GetMapping("/methods")
    @PreAuthorize("hasRole('MERCHANT')")
    @Operation(summary = "Get payment methods", description = "Récupérer toutes les méthodes de paiement du marchand")
    public ResponseEntity<?> getPaymentMethods(@AuthenticationPrincipal User user) {
        try {
            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            List<PaymentProvider> providers = paymentProviderService.getPaymentMethods(shop);
            List<PaymentMethodResponse> response = providers.stream()
                    .map(PaymentMethodResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiCollectResponse.success(response));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PatchMapping("/methods/{id}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(summary = "Update payment method", description = "Mettre à jour une méthode de paiement")
    public ResponseEntity<?> updatePaymentMethod(
            @PathVariable UUID id,
            @Valid @RequestBody AddPaymentMethodRequest request,
            @AuthenticationPrincipal User user
    ) {
        try {
            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            paymentProviderService.updatePaymentMethod(id, request);
            List<PaymentProvider> providers = paymentProviderService.getPaymentMethods(shop);
            List<PaymentMethodResponse> response = providers.stream()
                    .map(PaymentMethodResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiCollectResponse.success(response));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @DeleteMapping("/methods/{id}")
    @PreAuthorize("hasRole('SHOP')")
    @Operation(summary = "Delete payment method", description = "Supprimer une méthode de paiement")
    public ResponseEntity<?> deletePaymentMethod(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        try {
            Shop shop = shopRepository.findFirstByUser(user)
                    .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            paymentProviderService.deletePaymentMethod(id);

            List<PaymentProvider> providers = paymentProviderService.getPaymentMethods(shop);
            List<PaymentMethodResponse> response = providers.stream()
                    .map(PaymentMethodResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiCollectResponse.success(response));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}