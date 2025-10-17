package com.usetech.dvente.entities.products;

import com.usetech.dvente.entities.BaseModel;
import com.usetech.dvente.entities.users.Shop;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseModel {

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String shortDescription;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String state; // Enum possible

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePromo;

    private boolean isActive = true;

    private boolean isPromo = false;

    private String primaryImage;

    private int likeCount = 0;

    private int viewsCount = 0;

    private Integer stockCount;

    private Boolean inStock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "shop_author_id")
    private Shop shopAuthor;

    @ManyToMany
    @JoinTable(
            name = "product_keywords",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();

    @Column(length = 500)
    private String slug;

    @PrePersist
    @PreUpdate
    public void validateAndGenerateSlug() {
        if (pricePromo != null && pricePromo.compareTo(price) > 0) {
            throw new IllegalArgumentException("Le prix promo doit être ≤ au prix normal.");
        }

        this.slug = StringUtils.replace(title.toLowerCase().trim(), " ", "-");
    }

    public void like() {
        this.likeCount += 1;
    }

    public void addView() {
        this.viewsCount += 1;
    }

    @Override
    public String toString() {
        return title;
    }
}
