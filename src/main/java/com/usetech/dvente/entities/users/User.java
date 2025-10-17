package com.usetech.dvente.entities.users;

import com.usetech.dvente.entities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_email", columnList = "email", unique = true),
                @Index(name = "idx_phone", columnList = "phone", unique = true)
        })
@Builder
public class User extends BaseModel implements UserDetails {

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private UserRole role;

    @Builder.Default
    private boolean isStaff = false;

    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column
    private LocalDateTime dateJoined;

    @Column
    @Builder.Default
    private boolean emailConfirmed = false;

    @Column
    @Builder.Default
    private boolean phoneConfirmed = false;

    private String name;
    private String country;
    private String city;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String password;

    @Column(nullable = true)
    private String googleId;

    @Column(nullable = true)
    @Builder.Default
    private String provider = "GOOGLE";

    public String getAvatarUrl() {
        if (avatar != null && !avatar.isEmpty()) {
            return avatar;
        }
        return String.format(
                "https://ui-avatars.com/api/?background=0D8ABC&color=fff&name=%s",
                name != null ? name : "User"
        );
    }

    @Override
    public String toString() {
        return (phone != null && !phone.isEmpty()) ? phone : email;
    }

    // ----------------------
    // UserDetails methods
    // ----------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }



}
