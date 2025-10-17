package com.usetech.dvente.entities.users;

import lombok.Getter;

@Getter
public enum UserRole {

    EMPTY(""),
    CLIENT("Je veux faire des achats"),
    SHOP("Je veux vendre des produits");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

}
