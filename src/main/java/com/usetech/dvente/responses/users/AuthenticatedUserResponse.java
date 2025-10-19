package com.usetech.dvente.responses.users;

import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.ShopGallery;
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
    private ShopResponse shops;

    public static AuthenticatedUserResponse unauthenticated() {
        return AuthenticatedUserResponse.builder()
                .authenticated(false)
                .user(null)
                .shops(null)
                .build();
    }


    public static AuthenticatedUserResponse authenticated(UserResponse user, Shop marchand) {
        ShopResponse shopResponse = null;
        if (marchand != null) {
            shopResponse = ShopResponse.fromEntity(marchand, marchand.getGallery(), null);
        }
        return AuthenticatedUserResponse.builder()
                .authenticated(true)
                .user(user)
                .shops(shopResponse)
                .build();
    }


}
