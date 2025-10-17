package com.usetech.dvente.responses.users;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.responses.shops.ShopResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AuthenticatedUserResponse {

    private boolean authenticated;
    private UserResponse user;
    private ShopResponse marchand;

    public static AuthenticatedUserResponse unauthenticated() {
        return AuthenticatedUserResponse.builder()
                .authenticated(false)
                .user(null)
                .marchand(null)
                .build();
    }


    public  static AuthenticatedUserResponse authenticated(UserResponse user, Shop marchand, String apiUrl) {
        return AuthenticatedUserResponse.builder()
                .authenticated(true)
                .user(user)
                .marchand(marchand != null ? ShopResponse.fromEntity(marchand, apiUrl) : null)
                .build();
    }

}
