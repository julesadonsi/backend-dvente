package com.usetech.dvente.repositories.products;


import com.usetech.dvente.entities.products.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KeywordRepository extends JpaRepository<Keyword, UUID> {
    Optional<Keyword> findByName(String name);
}