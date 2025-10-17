package com.usetech.dvente.seeders;

import com.usetech.dvente.entities.products.Category;
import com.usetech.dvente.repositories.products.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<Category> categories = List.of(
                Category.builder()
                        .name("Électronique & Téléphonie")
                        .description("Smartphones, tablettes, ordinateurs et accessoires")
                        .slug("electronique-telephonie")
                        .image("electronics.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Mode & Vêtements")
                        .description("Vêtements traditionnels et modernes, chaussures")
                        .slug("mode-vetements")
                        .image("fashion.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Supermarché")
                        .description("Produits alimentaires, boissons, articles ménagers")
                        .slug("supermarche")
                        .image("supermarket.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Beauté & Soins")
                        .description("Cosmétiques, parfums, soins corporels")
                        .slug("beaute-soins")
                        .image("beauty.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Maison & Électroménager")
                        .description("Mobilier, décoration, appareils électroménagers")
                        .slug("maison-electromenager")
                        .image("home.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Informatique")
                        .description("Ordinateurs, imprimantes, accessoires informatiques")
                        .slug("informatique")
                        .image("computer.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Artisanat & Culture")
                        .description("Produits artisanaux, objets d'art, tissus locaux")
                        .slug("artisanat-culture")
                        .image("craft.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Auto & Moto")
                        .description("Pièces détachées, accessoires automobiles")
                        .slug("auto-moto")
                        .image("auto.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Agriculture & Élevage")
                        .description("Matériel agricole, produits d'élevage")
                        .slug("agriculture-elevage")
                        .image("agriculture.png")
                        .main(true)
                        .build(),

                Category.builder()
                        .name("Sport & Loisirs")
                        .description("Équipements sportifs, jeux, divertissement")
                        .slug("sport-loisirs")
                        .image("sports.png")
                        .main(true)
                        .build()
        );

        System.out.println("Seeding categories...");

        categories.forEach(category -> {
            categoryRepository.findByName(category.getName())
                    .ifPresentOrElse(
                            existing -> System.out.println("Category already exists: " + existing.getName()),
                            () -> {
                                categoryRepository.save(category);
                                System.out.println("Inserted: " + category.getName());
                            }
                    );
        });

        System.out.println("✅ Successfully seeded categories!");
        System.out.println("Total categories in DB: " + categoryRepository.count());
    }
}

