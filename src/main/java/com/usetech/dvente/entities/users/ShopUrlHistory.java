package com.usetech.dvente.entities.users;


import com.usetech.dvente.entities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopUrlHistory extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId")
    private Shop shop;

    @Column()
    private String oldShopUrl;

    @CreationTimestamp
    private LocalDateTime changedAt;

    @Override
    public String toString() {
        return oldShopUrl + " @ " + changedAt.toString();
    }
}
