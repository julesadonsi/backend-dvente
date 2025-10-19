package com.usetech.dvente.services.products;

import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductLike;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.products.ProductLikeRepository;
import com.usetech.dvente.repositories.products.ProductRepository;
import com.usetech.dvente.responses.products.PaginatedProductResponse;
import com.usetech.dvente.responses.products.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public boolean toggleLike(UUID productId, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        ProductLike existingLike = productLikeRepository
                .findByProductAndUser(product, user)
                .orElse(null);

        if (existingLike != null) {
            productLikeRepository.delete(existingLike);
            return false; // Unlike
        } else {
            ProductLike like = ProductLike.builder()
                    .product(product)
                    .user(user)
                    .build();
            productLikeRepository.save(like);
            return true; // Like
        }
    }

    @Transactional
    public void removeLike(UUID productId, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        ProductLike existingLike = productLikeRepository
                .findByProductAndUser(product, user)
                .orElse(null);

        if (existingLike != null) {
            productLikeRepository.delete(existingLike);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedProductResponse getUserLikedProducts(
            User user, Integer page, Integer pageSize, String apiUrl) {

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ProductLike> likePage = productLikeRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<ProductResponse> productResponses = likePage.getContent().stream()
                .map(like -> ProductResponse.fromEntity(like.getProduct(), apiUrl))
                .collect(Collectors.toList());

        return PaginatedProductResponse.of(
                productResponses,
                likePage.getTotalElements(),
                page,
                pageSize,
                likePage.getTotalPages(),
                likePage.hasNext()
        );
    }
}