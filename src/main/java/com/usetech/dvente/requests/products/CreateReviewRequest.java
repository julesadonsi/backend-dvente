package com.usetech.dvente.requests.products;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "La note est requise")
    @Min(value = 1, message = "La note doit être comprise entre 1 et 5")
    @Max(value = 5, message = "La note doit être comprise entre 1 et 5")
    private Integer rating;

    @NotBlank(message = "Le commentaire est requis")
    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String comment;
}