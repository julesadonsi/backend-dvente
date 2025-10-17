package com.usetech.dvente.entities.products;

import com.usetech.dvente.entities.BaseModel;
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
public class ProductImage extends BaseModel {

    private String image;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}