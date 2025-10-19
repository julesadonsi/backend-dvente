
package com.usetech.dvente.repositories.products;

import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductReview;
import com.usetech.dvente.entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {

    Page<ProductReview> findByProductOrderByCreatedAtDesc(Product product, Pageable pageable);

    Optional<ProductReview> findByProductAndUser(Product product, User user);

    Long countByProduct(Product product);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product = :product")
    Double findAverageRatingByProduct(@Param("product") Product product);
}