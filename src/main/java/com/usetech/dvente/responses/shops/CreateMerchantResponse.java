package com.usetech.dvente.responses.shops;

import com.usetech.dvente.responses.users.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantResponse {
    private ShopResponse marchand;
    private UserResponse user;
    private boolean authenticated;
}