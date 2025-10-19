package com.usetech.dvente.entities.users;

import com.usetech.dvente.entities.BaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop extends BaseModel {

    @Column(unique = true)
    private String shopUrl;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String whatsappNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String shopName;

    @Column(length = 13)
    private String numeroIfu;

    @Column(length = 50)
    private String domaineActivity;

    @Column(length = 50)
    private String regimeFiscale;

    @Column(length = 50)
    private String numRcm;

    private String ifuDocument;
    private String rcmDocument;
    private String logo;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private ShopStatus status = ShopStatus.WAITING;

    @Builder.Default
    private boolean visible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopView> views;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopGallery> gallery;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentProvider> paymentProviders;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopUrlHistory> shopnameHistory;

    @Transient
    public String getNameOfShop() {
        return shopName;
    }

    @Transient
    public double getProfileCompletion() {
        String[] fields = {
                whatsappNumber, address, city, country,
                description, shopName, numeroIfu
        };
        long filled = java.util.Arrays.stream(fields)
                .filter(f -> f != null && !f.isEmpty())
                .count();
        return Math.round((filled / (double) fields.length) * 10000.0) / 100.0;
    }

    @Override
    public String toString() {
        return shopName;
    }
}
