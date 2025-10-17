package com.usetech.dvente.entities.users;

import lombok.Getter;

@Getter
public enum ShopStatus {
    ACTIF("Actif"),
    INACTIF("Inactif"),
    BANNED("Banni"),
    REFUSED("Refusé"),
    WAITING("En attente de validation");

    private final String label;

    ShopStatus(String label) {
        this.label = label;
    }

}