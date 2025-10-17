package com.usetech.dvente.repositories.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShopGalleryRepository extends JpaRepository<ShopGallery, UUID> {

    List<ShopGallery> findByShopOrderByCreatedAtDesc(Shop shop);

    List<ShopGallery> findByShop_IdOrderByCreatedAtDesc(UUID shopId);
}