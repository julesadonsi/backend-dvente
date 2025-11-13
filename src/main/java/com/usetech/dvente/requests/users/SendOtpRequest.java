package com.usetech.dvente.requests.users;


import lombok.Data;

@Data
public class SendOtpRequest {
    private String phoneNumber;
}