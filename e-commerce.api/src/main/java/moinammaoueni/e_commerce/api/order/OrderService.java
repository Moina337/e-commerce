package moinammaoueni.e_commerce.api.order;

import moinammaoueni.e_commerce.api.order.dto.OrderManagementResponseDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderRequestDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderResponseDTO;
import moinammaoueni.e_commerce.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    // Client endpoints
    OrderResponseDTO createOrder(OrderRequestDTO request, User currentUser);

    Page<OrderResponseDTO> getMyOrders(User currentUser, Pageable pageable);

    OrderResponseDTO getMyOrder(Long orderId, User currentUser);

    void cancelMyOrder(Long orderId, User currentUser);

    // Admin endpoints
    Page<OrderManagementResponseDTO> getAllOrders(Pageable pageable);

    OrderManagementResponseDTO getOrderById(Long orderId);

    OrderManagementResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
}
