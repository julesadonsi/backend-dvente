
package com.usetech.dvente.services.products;

import com.usetech.dvente.entities.products.Product;
import com.usetech.dvente.entities.products.ProductReview;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.products.ProductRepository;
import com.usetech.dvente.repositories.products.ProductReviewRepository;
import com.usetech.dvente.requests.products.CreateReviewRequest;
import com.usetech.dvente.responses.products.PaginatedReviewResponse;
import com.usetech.dvente.responses.products.ProductReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PaginatedReviewResponse getProductReviews(UUID productId, Integer page, Integer pageSize) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ProductReview> reviewPage = productReviewRepository
                .findByProductOrderByCreatedAtDesc(product, pageable);

        List<ProductReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(ProductReviewResponse::fromEntity)
                .collect(Collectors.toList());

        // Calculer la note moyenne
        Double averageRating = productReviewRepository.findAverageRatingByProduct(product);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        return PaginatedReviewResponse.of(
                reviewResponses,
                reviewPage.getTotalElements(),
                page,
                pageSize,
                reviewPage.getTotalPages(),
                reviewPage.hasNext(),
                averageRating
        );
    }

    @Transactional
    public ProductReviewResponse createReview(UUID productId, CreateReviewRequest request, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Optional<ProductReview> existingReview = productReviewRepository
                .findByProductAndUser(product, user);

        if (existingReview.isPresent()) {
            throw new RuntimeException("Vous avez déjà laissé un avis pour ce produit");
        }

        ProductReview review = ProductReview.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        ProductReview savedReview = productReviewRepository.save(review);
        return ProductReviewResponse.fromEntity(savedReview);
    }
}