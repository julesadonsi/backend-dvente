package com.usetech.dvente.entities.products;

import com.usetech.dvente.entities.BaseModel;
import com.usetech.dvente.entities.users.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 500)
    private String review;

    private boolean isSeen = false;
}