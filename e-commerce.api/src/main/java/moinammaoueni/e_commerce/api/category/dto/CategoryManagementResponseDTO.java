package moinammaoueni.e_commerce.api.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO destiné au back-office.
 * Expose les informations de gestion incluant les timestamps.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryManagementResponseDTO {

    /**
     * Identifiant unique de la catégorie.
     */
    private Long id;

    /**
     * Nom de la catégorie.
     */
    private String name;

    /**
     * Slug de la catégorie pour les URLs.
     */
    private String slug;

    /**
     * Date et heure de création.
     */
    private LocalDateTime createdAt;

    /**
     * Date et heure de dernière mise à jour.
     */
    private LocalDateTime updatedAt;
}