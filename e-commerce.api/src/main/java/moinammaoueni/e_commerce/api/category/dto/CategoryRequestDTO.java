package moinammaoueni.e_commerce.api.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilisé pour créer et modifier une catégorie.
 * Le slug est généré automatiquement à partir du name côté serveur.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    /**
     * Nom de la catégorie.
     */
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(min = 1, max = 100, message = "Le nom doit être entre 1 et 100 caractères")
    private String name;
}
