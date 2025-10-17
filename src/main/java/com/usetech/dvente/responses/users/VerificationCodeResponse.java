package com.usetech.dvente.responses.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeResponse {
    private String message;
    private String email;
    private Integer expiresInMinutes;
}