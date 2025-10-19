
package com.usetech.dvente.services.payments;

import com.usetech.dvente.entities.users.PaymentProvider;
import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.repositories.payments.PaymentProviderRepository;
import com.usetech.dvente.requests.payments.AddPaymentMethodRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentProviderService {

    private final PaymentProviderRepository paymentProviderRepository;

    @Transactional
    public PaymentProvider addPaymentMethod(Shop shop, AddPaymentMethodRequest request) {
        PaymentProvider provider = PaymentProvider.builder()
                .shop(shop)
                .name(request.getName())
                .phone(request.getPhone())
                .active(request.getActive() != null ? request.getActive() : false)
                .build();

        return paymentProviderRepository.save(provider);
    }

    @Transactional(readOnly = true)
    public List<PaymentProvider> getPaymentMethods(Shop shop) {
        return paymentProviderRepository.findByShopOrderByCreatedAtDesc(shop);
    }

    @Transactional
    public PaymentProvider updatePaymentMethod(UUID providerId, AddPaymentMethodRequest request) {
        PaymentProvider provider = paymentProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée"));

        if (request.getName() != null && !request.getName().isBlank()) {
            provider.setName(request.getName());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            provider.setPhone(request.getPhone());
        }

        if (request.getActive() != null) {
            provider.setActive(request.getActive());
        }

        return paymentProviderRepository.save(provider);
    }

    @Transactional
    public void deletePaymentMethod(UUID providerId) {
        paymentProviderRepository.deleteById(providerId);
    }
}