package moinammaoueni.e_commerce.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private String image;
    private CategorySummaryDTO category;
}
