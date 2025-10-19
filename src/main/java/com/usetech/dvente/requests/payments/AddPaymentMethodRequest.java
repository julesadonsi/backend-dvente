package com.usetech.dvente.requests.payments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentMethodRequest {

    @NotBlank(message = "Le nom du fournisseur de paiement est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String name;

    @NotBlank(message = "Le numéro de téléphone est requis")
    @Size(max = 50, message = "Le numéro de téléphone ne doit pas dépasser 50 caractères")
    private String phone;

    private Boolean active = false;
}