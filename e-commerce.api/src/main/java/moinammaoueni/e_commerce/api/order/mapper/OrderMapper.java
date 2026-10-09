package moinammaoueni.e_commerce.api.order.mapper;

import moinammaoueni.e_commerce.api.order.Order;
import moinammaoueni.e_commerce.api.order.OrderItem;
import moinammaoueni.e_commerce.api.order.dto.OrderItemResponseDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderManagementResponseDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderResponseDTO;
import moinammaoueni.e_commerce.api.order.dto.ProductSummaryDTO;
import moinammaoueni.e_commerce.api.order.dto.UserSummaryDTO;
import moinammaoueni.e_commerce.api.product.Product;
import moinammaoueni.e_commerce.api.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", expression = "java(order.getItems().stream().map(this::toOrderItemResponseDTO).toList())")
    OrderResponseDTO toOrderResponseDTO(Order order);

    @Mapping(target = "client", expression = "java(toUserSummary(order.getUser()))")
    @Mapping(target = "items", expression = "java(order.getItems().stream().map(this::toOrderItemResponseDTO).toList())")
    OrderManagementResponseDTO toOrderManagementResponseDTO(Order order);

    default OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProduct(toProductSummary(item.getProduct()));
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    default ProductSummaryDTO toProductSummary(Product product) {
        if (product == null) {
            return null;
        }

        String firstImageUrl = product.getImages() == null || product.getImages().isEmpty()
                ? null
                : "/api/images/" + product.getImages().getFirst().getId() + "/content";

        return new ProductSummaryDTO(product.getId(), product.getName(), product.getSlug(), firstImageUrl);
    }

    default UserSummaryDTO toUserSummary(User user) {
        if (user == null) {
            return null;
        }

        return new UserSummaryDTO(user.getId(), user.getEmail(), user.getName());
    }
}