package com.usetech.dvente.responses.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCollectResponse<T> {

    private T data;
    private String status;

    public static <T> ApiCollectResponse<T> success(T data) {
        return ApiCollectResponse.<T>builder()
                .data(data)
                .status("success")
                .build();
    }
}