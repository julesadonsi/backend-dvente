package com.usetech.dvente.controllers.admin.shops;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.repositories.shops.ShopRepository;
import com.usetech.dvente.services.shops.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin/shops")
@RequiredArgsConstructor
public class AdminShopController {

    private final ShopService shopService;
    private final ShopRepository shopRepository;


    @GetMapping("{shopId}/validate")
    public String shopDetails(
            @PathVariable UUID shopId, Model model, HttpServletRequest request)
    {
        Shop shop  = shopRepository.findById(shopId).orElseThrow(() -> new RuntimeException("Shop not found"));
        shopService.validateShop(shop);

        model.addAttribute("successMessage", "Boutique activée avec succès !");
        return "redirect:/admin/shops";

    }
}
