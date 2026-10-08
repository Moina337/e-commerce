package moinammaoueni.e_commerce.api.order;

import jakarta.validation.Valid;
import moinammaoueni.e_commerce.api.order.dto.OrderRequestDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderResponseDTO;
import moinammaoueni.e_commerce.api.security.CurrentUser;
import moinammaoueni.e_commerce.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUser currentUser;

    public OrderController(OrderService orderService, CurrentUser currentUser) {
        this.orderService = orderService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        User connectedUser = currentUser.get();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, connectedUser));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User connectedUser = currentUser.get();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getMyOrders(connectedUser, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getMyOrder(@PathVariable Long id) {
        User connectedUser = currentUser.get();
        return ResponseEntity.ok(orderService.getMyOrder(id, connectedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMyOrder(@PathVariable Long id) {
        User connectedUser = currentUser.get();
        orderService.cancelMyOrder(id, connectedUser);
        return ResponseEntity.noContent().build();
    }
}