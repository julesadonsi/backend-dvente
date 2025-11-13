package com.usetech.dvente.repositories;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.entities.users.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    boolean existsByRoleAndIsActiveTrue(UserRole role);
    /**
     * Trouve un utilisateur par email
     */
    Optional<User> findByEmail(String email);

    /**
     * Trouve un utilisateur par téléphone
     */
    Optional<User> findByPhone(String phone);

    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :userId")
    void updateUserEmail(UUID userId, String email);

    /**
     * Met à jour le rôle d'un utilisateur
     * @param role role user
     * @param userId id uuid for user
     */
    @Modifying
    @Query("UPDATE User u SET u.role = :role where u.id = :userId")
    void updateUserRole(UserRole role, UUID userId);
}