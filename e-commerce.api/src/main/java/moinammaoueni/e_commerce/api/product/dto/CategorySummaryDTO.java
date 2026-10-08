package moinammaoueni.e_commerce.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryDTO {

    private Long id;
    private String name;
    private String slug;
}
