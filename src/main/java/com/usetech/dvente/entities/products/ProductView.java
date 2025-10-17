package com.usetech.dvente.entities.products;

import com.usetech.dvente.entities.BaseModel;
import com.usetech.dvente.entities.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductView extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String ipAddress;

    @Override
    public String toString() {
        return product.getTitle();
    }
}
