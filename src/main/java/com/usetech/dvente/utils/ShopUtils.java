package com.usetech.dvente.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class ShopUtils {

    /**
     * Convertit une chaîne de caractères en slug
     * Exemple : "Mon Super Shop!" → "mon-super-shop"
     */
    public static String slugify(String input) {
        if (input == null) {
            return null;
        }

        String nowhitespace = Pattern.compile("\\s+").matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
        return slug.toLowerCase();
    }

    /**
     * Supprime le symbole @ du début d'une URL de boutique si présent
     * Exemple : "@ma-boutique" → "ma-boutique"
     */
    public static String removeAtSymbol(String shopUrl) {
        if (shopUrl == null) {
            return null;
        }
        return shopUrl.startsWith("@") ? shopUrl.substring(1) : shopUrl;
    }
}
