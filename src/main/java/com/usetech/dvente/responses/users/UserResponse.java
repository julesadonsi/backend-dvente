package com.usetech.dvente.responses.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.usetech.dvente.configs.ApiConfig;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.entities.users.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private static ApiConfig apiConfig;

    @Autowired
    public void setApiConfig(ApiConfig apiConfig) {
        UserResponse.apiConfig = apiConfig;
    }

    @NotNull
    private UUID id;

    private String name;

    @Email
    private String email;

    private UserRole role;
    private String phone;
    private String avatar;
    private String country;
    private String city;
    private boolean phone_confirmed;
    private boolean email_confirmed;


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date_joined;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateUpdated;
    private boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserResponse fromUser(User user) {
        String baseUrl = apiConfig.getApiUrl();
        String fullAvatarUrl = user.getAvatar() != null ? baseUrl + user.getAvatar() : null;

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .avatar(fullAvatarUrl)
                .country(user.getCountry())
                .city(user.getCity())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .date_joined(user.getDateJoined())
                .isActive(user.isActive())
                .phone_confirmed(user.isPhoneConfirmed())
                .email_confirmed(user.isEmailConfirmed())
                .build();
    }
}
