package com.usetech.dvente.repositories.products;

import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.users.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Récupère les produits actifs avec pagination
     * Utilise JOIN FETCH pour éviter le problème N+1
     */
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.shopAuthor
        WHERE p.isActive = true
        ORDER BY p.createdAt DESC
    """)
    Page<Product> findAllActiveProducts(Pageable pageable);

    /**
     * Récupère les produits actifs par catégorie avec pagination
     */
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.category c
        LEFT JOIN FETCH p.shopAuthor
        WHERE p.isActive = true AND c.id = :categoryId
        ORDER BY p.createdAt DESC
    """)
    Page<Product> findActiveProductsByCategory(
            @Param("categoryId") UUID categoryId,
            Pageable pageable
    );

    /**
     * Compte le nombre d'avis pour un produit
     */
    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.product.id = :productId")
    Long countReviewsByProductId(@Param("productId") UUID productId);

    /**
     * Récupère un produit avec toutes ses relations (optimisé pour éviter N+1)
     */
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.shopAuthor
        LEFT JOIN FETCH p.otherImages
        LEFT JOIN FETCH p.keywords
        WHERE p.id = :productId
    """)
    Optional<Product> findProductWithDetails(@Param("productId") UUID productId);


    /**
     * Trouve tous les produits d'un marchand avec pagination et optimisation N+1
     */
    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.category
        LEFT JOIN FETCH p.shopAuthor
        LEFT JOIN FETCH p.otherImages
        WHERE p.shopAuthor = :shop
        ORDER BY p.createdAt DESC
    """)
    Page<Product> findByShopAuthorWithDetails(@Param("shop") Shop shop, Pageable pageable);

    /**
     * Compte le total des produits pour un marchand
     */
    Long countByShopAuthor(Shop shop);

    /**
     * Compte le nombre d'avis pour une liste de produits
     */
    @Query("SELECT p.id as productId, COUNT(r) as reviewCount FROM Product p " +
            "LEFT JOIN p.reviews r WHERE p IN :products GROUP BY p.id")
    List<Object[]> countReviewsForProducts(@Param("products") List<Product> products);




    /**
     * Trouve tous les produits d'une boutique avec pagination
     */
    Page<Product> findByShopAuthor(Shop shop, Pageable pageable);

    /**
     * Trouve les produits d'une boutique par nom avec pagination
     */
    Page<Product> findByShopAuthorAndNameContainingIgnoreCase(
            Shop shop,
            String name,
            Pageable pageable
    );

    /**
     * Trouve tous les produits d'une boutique (sans pagination)
     */
    List<Product> findByShopAuthor(Shop shop);

    /**
     * Trouve les produits actifs d'une boutique avec pagination
     */
    Page<Product> findByShopAuthorAndIsActiveTrue(Shop shop, Pageable pageable);

    /**
     * Trouve les produits actifs d'une boutique par nom avec pagination
     */
    Page<Product> findByShopAuthorAndIsActiveTrueAndNameContainingIgnoreCase(
            Shop shop,
            String name,
            Pageable pageable
    );
}