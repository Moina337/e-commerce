package moinammaoueni.e_commerce.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moinammaoueni.e_commerce.api.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponseDTO> items;
    private LocalDateTime createdAt;
}
