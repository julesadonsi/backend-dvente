package com.usetech.dvente.requests.users;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phoneNumber;
    private String code;
}
