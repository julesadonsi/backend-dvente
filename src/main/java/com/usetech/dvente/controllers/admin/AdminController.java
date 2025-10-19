package com.usetech.dvente.controllers.admin;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopStatus;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.services.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final UserService userService;

    /**
     * Page de connexion admin
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null)
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
        if (logout != null)
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        return "admin/login";
    }

    /**
     * Tableau de bord admin
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalShops = shopRepository.count();
        long activeShops = shopRepository.countByVisible(true);
        long pendingShops = shopRepository.countByStatus(ShopStatus.WAITING);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalShops", totalShops);
        model.addAttribute("activeShops", activeShops);
        model.addAttribute("pendingShops", pendingShops);
        model.addAttribute("pageTitle", "Tableau de bord");

        return "admin/dashboard";
    }

    /**
     * Liste des utilisateurs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir,
                        Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("pageTitle", "Gestion des utilisateurs");

        return "admin/users";
    }

    /**
     * Détails utilisateur
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable UUID id, Model model, HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "redirect:/admin/users";
        }

        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Détails utilisateur - " + user.getName());
        model.addAttribute("currentURI", request.getRequestURI());

        return "admin/user-details";
    }

    /**
     * Liste des boutiques
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/shops")
    public String shops(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir,
                        @RequestParam(required = false) String status,
                        Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Shop> shops;

        if (status != null && !status.trim().isEmpty() && !status.equals("ALL")) {
            try {
                ShopStatus shopStatus = ShopStatus.valueOf(status.toUpperCase());
                shops = shopRepository.findByStatus(shopStatus, pageable);
            } catch (IllegalArgumentException e) {
                shops = shopRepository.findAll(pageable);
            }
        } else {
            shops = shopRepository.findAll(pageable);
        }

        model.addAttribute("shops", shops);
        model.addAttribute("pageTitle", "Gestion des boutiques");
        return "admin/shops";
    }

    /**
     * Détails boutique
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/shops/{id}")
    public String shopDetails(@PathVariable UUID id, Model model, HttpServletRequest request) {
        Optional<Shop> shopOpt = shopRepository.findById(id);
        if (shopOpt.isEmpty()) {
            model.addAttribute("error", "Boutique non trouvée");
            return "redirect:/admin/shops";
        }

        Shop shop = shopOpt.get();
        model.addAttribute("shop", shop);
        model.addAttribute("pageTitle", "Détails boutique - " + shop.getShopName());
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/shop-details";
    }

    /**
     * Approuver / rejeter boutique
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/shops/{id}/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveShop(@PathVariable UUID id) {
        return updateShopStatus(id, ShopStatus.ACTIF, "Boutique approuvée avec succès");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/shops/{id}/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rejectShop(@PathVariable UUID id) {
        return updateShopStatus(id, ShopStatus.REFUSED, "Boutique rejetée");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/shops/{id}/toggle-visibility")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleShopVisibility(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Shop> shopOpt = shopRepository.findById(id);
        if (shopOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Boutique non trouvée");
            return ResponseEntity.notFound().build();
        }

        Shop shop = shopOpt.get();
        shop.setVisible(!shop.isVisible());
        shopRepository.save(shop);

        response.put("success", true);
        response.put("message", "Visibilité mise à jour");
        response.put("newVisibility", shop.isVisible());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> updateShopStatus(UUID id, ShopStatus newStatus, String successMessage) {
        Map<String, Object> response = new HashMap<>();
        Optional<Shop> shopOpt = shopRepository.findById(id);
        if (shopOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Boutique non trouvée");
            return ResponseEntity.notFound().build();
        }

        Shop shop = shopOpt.get();
        shop.setStatus(newStatus);
        shopRepository.save(shop);

        response.put("success", true);
        response.put("message", successMessage);
        response.put("newStatus", newStatus.toString());
        return ResponseEntity.ok(response);
    }
}
