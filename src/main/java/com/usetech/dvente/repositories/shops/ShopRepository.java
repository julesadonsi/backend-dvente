
package com.usetech.dvente.repositories.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopStatus;
import com.usetech.dvente.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    /**
     * Trouve une boutique par son URL unique
     */
    Optional<Shop> findByShopUrl(String shopUrl);

    /**
     * Vérifie si une boutique existe avec cette URL
     */
    boolean existsByShopUrl(String shopUrl);

    /**
     * Trouve toutes les boutiques d'un utilisateur
     */
    List<Shop> findByUser(User user);

    /**
     * Trouve toutes les boutiques d'un utilisateur par ID
     */
    List<Shop> findByUser_Id(UUID userId);

    /**
     * Trouve toutes les boutiques visibles
     */
    List<Shop> findByVisibleTrue();

    /**
     * Trouve toutes les boutiques par statut
     */
    List<Shop> findByStatus(ShopStatus status);

    /**
     * Trouve toutes les boutiques visibles avec un statut spécifique
     */
    List<Shop> findByVisibleTrueAndStatus(ShopStatus status);

    /**
     * Trouve une boutique par email
     */
    Optional<Shop> findByEmail(String email);

    /**
     * Trouve les boutiques par ville
     */
    List<Shop> findByCity(String city);

    /**
     * Trouve les boutiques par pays
     */
    List<Shop> findByCountry(String country);

    /**
     * Trouve les boutiques par ville et pays
     */
    List<Shop> findByCityAndCountry(String city, String country);

    /**
     * Recherche de boutiques par nom (insensible à la casse)
     */
    List<Shop> findByShopNameContainingIgnoreCase(String shopName);

    /**
     * Trouve les boutiques actives d'un utilisateur
     */
    @Query("SELECT s FROM Shop s WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.visible = true")
    List<Shop> findActiveShopsByUserId(@Param("userId") UUID userId);

    /**
     * Compte le nombre de boutiques d'un utilisateur
     */
    long countByUser_Id(UUID userId);

    /**
     * Compte le nombre de boutiques par statut
     */
    long countByStatus(ShopStatus status);

    /**
     * Trouve les boutiques avec un taux de complétion minimum
     * Note: Cette méthode nécessite une logique personnalisée dans le service
     */
    @Query("SELECT s FROM Shop s WHERE s.visible = true ORDER BY s.createdAt DESC")
    List<Shop> findAllVisibleOrderByNewest();

    /**
     * Trouve les boutiques les plus populaires (par nombre de vues)
     */
    @Query("SELECT s FROM Shop s LEFT JOIN s.views v " +
            "WHERE s.visible = true AND s.status = 'ACTIVE' " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(v) DESC")
    List<Shop> findMostPopularShops();

    /**
     * Recherche avancée de boutiques
     */
    @Query("SELECT s FROM Shop s WHERE " +
            "s.visible = true AND s.status = 'ACTIVE' AND " +
            "(LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.domaineActivity) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Shop> searchShops(@Param("keyword") String keyword);

    /**
     * Trouve les boutiques par domaine d'activité
     */
    List<Shop> findByDomaineActivityAndVisibleTrueAndStatus(String domaineActivity, ShopStatus status);

    /**
     * Vérifie si un utilisateur possède déjà une boutique active
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Shop s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    boolean userHasActiveShop(@Param("userId") UUID userId);

    /**
     * Trouve les boutiques nécessitant une vérification (statut WAITING)
     */
    @Query("SELECT s FROM Shop s WHERE s.status = 'WAITING' ORDER BY s.createdAt ASC")
    List<Shop> findShopsPendingVerification();

    boolean existsByUserId(UUID userId);
}