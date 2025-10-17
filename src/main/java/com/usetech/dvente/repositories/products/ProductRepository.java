package com.usetech.dvente.repositories.products;

import com.usetech.dvente.entities.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}