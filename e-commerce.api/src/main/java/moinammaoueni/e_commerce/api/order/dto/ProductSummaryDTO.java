package moinammaoueni.e_commerce.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {

    private Long id;
    private String name;
    private String slug;
    private String image;
}
