package com.usetech.dvente.requests.shops;

import com.usetech.dvente.requests.files.ValidDocument;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShopRequest {

    @Size(max = 255, message = "Le nom de la boutique ne doit pas dépasser 255 caractères")
    private String shopName;

    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "L'URL de la boutique doit être un slug valide")
    private String shopUrl;

    @Email(message = "L'email doit être valide")
    private String email;

    private String address;

    private String whatsappNumber;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;

    private String numeroIfu;

    private String numRcm;

    private String city;

    private String country;

    private String domaineActivity;

    private String regimeFiscale;

}
