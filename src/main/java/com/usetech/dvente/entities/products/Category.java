package com.usetech.dvente.entities.products;

import com.usetech.dvente.entities.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.StringUtils;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseModel {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String slug;

    private boolean main = false;

    private String image;

    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent;

    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (slug == null || slug.isBlank()) {
            this.slug = StringUtils.replace(name.toLowerCase().trim(), " ", "-");
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
