package com.usetech.dvente.responses;

import com.usetech.dvente.responses.users.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserResponse user;
}
