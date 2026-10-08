package moinammaoueni.e_commerce.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    private ProductSummaryDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
