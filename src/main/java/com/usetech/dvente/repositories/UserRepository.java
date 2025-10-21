package com.usetech.dvente.repositories;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.entities.users.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Recherche d'utilisateurs par email avec pagination
     * Permet de faire une recherche partielle (contient) sur l'email
     */
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    /**
     * Recherche avancée d'utilisateurs par nom, email ou téléphone avec pagination
     * Plus flexible que la méthode existante
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findAllWithSearch(@Param("search") String search, Pageable pageable);


    boolean existsByRoleAndIsActiveTrue(UserRole role);
    /**
     * Trouve un utilisateur par email
     */
    Optional<User> findByEmail(String email);

    /**
     * Trouve un utilisateur par téléphone
     */
    Optional<User> findByPhone(String phone);

    /**
     * Trouve un utilisateur par Google ID
     */
    Optional<User> findByGoogleId(String googleId);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un téléphone existe déjà
     */
    boolean existsByPhone(String phone);

    /**
     * Trouve tous les utilisateurs actifs
     */
    List<User> findByIsActiveTrue();

    /**
     * Trouve tous les utilisateurs inactifs
     */
    List<User> findByIsActiveFalse();

    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<User> findByRole(UserRole role);

    /**
     * Trouve tous les utilisateurs avec email confirmé
     */
    List<User> findByEmailConfirmedTrue();

    /**
     * Trouve tous les utilisateurs avec téléphone confirmé
     */
    List<User> findByPhoneConfirmedTrue();

    /**
     * Recherche d'utilisateurs par nom ou email (insensible à la casse) avec pagination
     */
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    /**
     * Recherche d'utilisateurs par nom (insensible à la casse)
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Recherche d'utilisateurs par email (insensible à la casse)
     */
    List<User> findByEmailContainingIgnoreCase(String email);

    /**
     * Trouve tous les utilisateurs créés après une certaine date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Trouve tous les utilisateurs créés entre deux dates
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Trouve tous les utilisateurs par ville
     */
    List<User> findByCity(String city);

    /**
     * Trouve tous les utilisateurs par pays
     */
    List<User> findByCountry(String country);

    /**
     * Trouve tous les utilisateurs par ville et pays
     */
    List<User> findByCityAndCountry(String city, String country);

    /**
     * Compte le nombre d'utilisateurs actifs
     */
    long countByIsActiveTrue();

    /**
     * Compte le nombre d'utilisateurs inactifs
     */
    long countByIsActiveFalse();

    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    long countByRole(UserRole role);

    /**
     * Compte le nombre d'utilisateurs avec email confirmé
     */
    long countByEmailConfirmedTrue();

    /**
     * Trouve les utilisateurs les plus récents
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(Pageable pageable);

    /**
     * Recherche avancée d'utilisateurs
     */
    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Trouve tous les administrateurs
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' OR u.isStaff = true")
    List<User> findAdminUsers();

    /**
     * Trouve tous les utilisateurs qui ont des boutiques
     */
    @Query("SELECT DISTINCT u FROM User u JOIN Shop s ON u.id = s.user.id")
    List<User> findUsersWithShops();

    /**
     * Trouve tous les utilisateurs sans boutique
     */
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT s.user.id FROM Shop s)")
    List<User> findUsersWithoutShops();

    /**
     * Statistiques des utilisateurs par mois
     */
    @Query("SELECT YEAR(u.createdAt), MONTH(u.createdAt), COUNT(u) " +
            "FROM User u " +
            "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) " +
            "ORDER BY YEAR(u.createdAt) DESC, MONTH(u.createdAt) DESC")
    List<Object[]> getUserRegistrationStats();

    /**
     * Trouve les utilisateurs inactifs depuis une certaine date
     */
    @Query("SELECT u FROM User u WHERE u.updatedAt < :date AND u.isActive = true")
    List<User> findInactiveUsersSince(@Param("date") LocalDateTime date);


    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :userId")
    int updateUserEmail(UUID userId, String email);
}