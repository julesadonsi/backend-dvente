package com.usetech.dvente.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WhatsAppRequest {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String to;

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    private String mediaUrl;
}
