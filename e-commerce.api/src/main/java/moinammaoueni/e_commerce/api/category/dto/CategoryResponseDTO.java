package moinammaoueni.e_commerce.api.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO destiné aux données exposées publiquement.
 * Les timestamps ne sont pas exposés.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

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
}