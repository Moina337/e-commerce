package moinammaoueni.e_commerce.api.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom du produit ne doit pas dépasser 150 caractères")
    private String name;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal price;

    @NotNull(message = "La quantité en stock est obligatoire")
    private Integer stockQuantity;

    private String image;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;
}
