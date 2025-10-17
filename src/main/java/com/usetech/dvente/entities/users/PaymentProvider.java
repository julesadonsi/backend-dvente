package com.usetech.dvente.entities.users;

import com.usetech.dvente.entities.BaseModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProvider extends BaseModel {

    @Column(length = 50)
    private String name;

    @Builder.Default
    private boolean active = false;

    @Column(length = 50)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId")
    private Shop shop;
}

