package com.usetech.dvente.responses.products;

import com.usetech.dvente.entities.products.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordResponse {

    private UUID id;
    private String name;
    private String color;
    private Boolean popular;

    public static KeywordResponse fromEntity(Keyword keyword) {
        return KeywordResponse.builder()
                .id(keyword.getId())
                .name(keyword.getName())
                .color(keyword.getColor())
                .popular(keyword.isPopular())
                .build();
    }
}