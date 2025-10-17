package com.usetech.dvente.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email ne peut pas être vide")
    private String email;


    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;
}
