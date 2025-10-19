package com.usetech.dvente.repositories.payments;

import com.usetech.dvente.entities.users.PaymentProvider;
import com.usetech.dvente.entities.users.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, UUID> {

    List<PaymentProvider> findByShop(Shop shop);

    List<PaymentProvider> findByShopOrderByCreatedAtDesc(Shop shop);

    List<PaymentProvider> findByShop_Id(UUID shopId);
}