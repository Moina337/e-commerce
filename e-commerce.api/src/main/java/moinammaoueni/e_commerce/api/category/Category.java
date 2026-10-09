package moinammaoueni.e_commerce.api.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Entité JPA représentant une catégorie de produits.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.slug = generateSlug(this.name);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.name != null && !this.name.isBlank()) {
            this.slug = generateSlug(this.name);
        }
    }

    private String generateSlug(String value) {
        String safeValue = value == null ? "" : value.trim();
        String normalized = Normalizer.normalize(safeValue, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-$", "")
                .trim();

        return slug.isEmpty() ? "category" : slug;
    }
}