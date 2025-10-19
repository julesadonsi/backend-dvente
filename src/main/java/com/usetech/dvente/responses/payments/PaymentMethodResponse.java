package com.usetech.dvente.responses.payments;

import com.usetech.dvente.entities.users.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {

    private UUID id;
    private String name;
    private String phone;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentMethodResponse fromEntity(PaymentProvider provider) {
        return PaymentMethodResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .phone(provider.getPhone())
                .active(provider.isActive())
                .createdAt(provider.getCreatedAt())
                .updatedAt(provider.getUpdatedAt())
                .build();
    }
}