package com.usetech.dvente.requests.shops;

import com.usetech.dvente.requests.files.ValidDocument;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShopRequest {

    @NotBlank(message = "Le nom de la boutique est requis")
    @Size(max = 255, message = "Le nom de la boutique ne doit pas dépasser 255 caractères")
    private String shopName;

    @NotBlank(message = "L'URL de la boutique est requise")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "L'URL de la boutique doit être un slug valide")
    private String shopUrl;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "L'adresse est requise")
    private String address;

    @NotBlank(message = "Le numéro WhatsApp est requis")
    private String whatsappNumber;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;

    @NotBlank(message = "Le numéro IFU est requis")
    private String numeroIfu;

    @NotBlank(message = "Le numéro RCM est requis")
    private String numRcm;

    @NotBlank(message = "La ville est requise")
    private String city;

    @NotBlank(message = "Le pays est requis")
    private String country;

    @NotBlank(message = "Le domaine d'activité est requis")
    private String domaineActivity;

    private String regimeFiscale;

    @NotNull(message = "Le document IFU est requis")
    @ValidDocument
    private MultipartFile ifuDocument;

    @NotNull(message = "Le document RCM est requis")
    @ValidDocument
    private MultipartFile rcmDocument;
}