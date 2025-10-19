package com.usetech.dvente.entities.users;

import lombok.Getter;

@Getter
public enum ShopStatus {
    ACTIF("Actif"),
    SUSPENDED("Suspendu"),
    REFUSED("Refusé"),
    WAITING("En attente de validation"),
    INACTIF("Inactif"),
    BANNED("Banni");

    private final String label;

    ShopStatus(String label) {
        this.label = label;
    }

}