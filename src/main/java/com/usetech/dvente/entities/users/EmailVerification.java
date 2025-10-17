package com.usetech.dvente.entities.users;

import com.usetech.dvente.entities.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_email_code", columnList = "email, code", unique = true),
                @Index(name = "idx_expired_at", columnList = "expiredAt")
        }
)
public class EmailVerification  extends BaseModel {

    @Column(length = 50)
    private String email;

    @Column(length = 6, unique = true)
    private String code;

    private LocalDateTime expiredAt;

    @Column
    @Builder.Default
    private boolean isUsed = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void generateCode() {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        this.code = sb.toString();
        this.expiredAt = LocalDateTime.now().plusMinutes(15);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    @Override
    public String toString() {
        return email + "-" + code;
    }
}

