package com.usetech.dvente.requests.users;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

    @Email(message = "Email must be valid")
    private String email;
    private String name;

    private String phone;

    private String country;

    private String city;

    private MultipartFile avatar;
}
