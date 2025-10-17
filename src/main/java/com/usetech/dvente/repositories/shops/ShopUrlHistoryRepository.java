package com.usetech.dvente.repositories.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopUrlHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopUrlHistoryRepository extends JpaRepository<ShopUrlHistory, UUID> {

    Optional<ShopUrlHistory> findFirstByShopOrderByChangedAtDesc(Shop shop);

    Optional<ShopUrlHistory> findFirstByShop_IdOrderByChangedAtDesc(UUID shopId);
}