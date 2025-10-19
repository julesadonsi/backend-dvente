package com.usetech.dvente.repositories.products;

import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductLike;
import com.usetech.dvente.entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductLikeRepository extends JpaRepository<ProductLike, UUID> {

    Optional<ProductLike> findByProductAndUser(Product product, User user);

    Page<ProductLike> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Long countByProduct(Product product);
}