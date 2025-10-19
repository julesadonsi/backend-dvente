
package com.usetech.dvente.repositories.products;

import com.usetech.dvente.entities.products.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

    /**
     * Trouve un keyword par son nom
     */
    Optional<Keyword> findByName(String name);

    /**
     * Trouve tous les keywords populaires
     */
    List<Keyword> findByPopularTrue();

    /**
     * Trouve les keywords par nom (contient, insensible à la casse)
     */
    List<Keyword> findByNameContainingIgnoreCase(String name);

    /**
     * Vérifie si un keyword existe par nom
     */
    boolean existsByName(String name);
}