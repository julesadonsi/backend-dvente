package com.usetech.dvente.seeders;


import com.usetech.dvente.entities.products.Keyword;
import com.usetech.dvente.repositories.products.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class KeywordSeeder implements CommandLineRunner {

    private final KeywordRepository keywordRepository;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Seeding keywords...");

        Map<String, String> colors = Map.ofEntries(
                Map.entry("tech", "#007bff"),          // Bleu
                Map.entry("mode", "#e83e8c"),          // Rose
                Map.entry("beaute", "#ff69b4"),        // Rose clair
                Map.entry("maison", "#28a745"),        // Vert
                Map.entry("electromenager", "#6f42c1"),// Violet
                Map.entry("alimentation", "#fd7e14"),  // Orange
                Map.entry("sport", "#dc3545"),         // Rouge
                Map.entry("auto", "#20c997"),          // Turquoise
                Map.entry("agriculture", "#198754"),   // Vert foncé
                Map.entry("etat", "#6c757d"),          // Gris
                Map.entry("service", "#17a2b8")        // Cyan
        );

        Set<String> popularKeywords = Set.of(
                "smartphone", "ordinateur", "robe", "chaussures", "parfum",
                "meuble", "voiture", "promotion", "solde", "nouveau",
                "populaire", "tendance"
        );

        Map<String, List<String>> keywordsData = Map.of(
                "tech", List.of(
                        "smartphone", "ordinateur", "laptop", "tablette", "écouteurs",
                        "casque", "télévision", "appareil photo", "caméra", "console",
                        "jeux vidéo", "accessoires tech", "chargeur", "batterie",
                        "câble", "clavier", "souris"
                )
        );

        int createdCount = 0;

        try {
            for (Map.Entry<String, List<String>> entry : keywordsData.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();

                for (String keyword : keywords) {
                    String keywordLower = keyword.toLowerCase();

                    if (keywordRepository.findByName(keywordLower).isEmpty()) {
                        Keyword newKeyword = Keyword.builder()
                                .name(keywordLower)
                                .color(colors.get(category))
                                .popular(popularKeywords.contains(keywordLower))
                                .build();

                        keywordRepository.save(newKeyword);
                        createdCount++;
                        System.out.println("Inserted keyword: " + keywordLower);
                    }
                }
            }

            System.out.printf("✅ Successfully seeded keywords. Created %d new keywords.%n", createdCount);
            long total = keywordRepository.count();
            System.out.println("Total keywords in database: " + total);

        } catch (Exception e) {
            System.err.println("❌ An error occurred while seeding keywords: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

