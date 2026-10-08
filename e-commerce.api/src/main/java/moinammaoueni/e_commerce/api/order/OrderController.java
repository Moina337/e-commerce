package moinammaoueni.e_commerce.api.order;

import jakarta.validation.Valid;
import moinammaoueni.e_commerce.api.order.dto.OrderRequestDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderResponseDTO;
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

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        // TODO: Récupérer l'utilisateur authentifié depuis le contexte
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, mockUser));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // TODO: Récupérer l'utilisateur authentifié depuis le contexte
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getMyOrders(mockUser, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getMyOrder(@PathVariable Long id) {
        // TODO: Récupérer l'utilisateur authentifié depuis le contexte
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");

        return ResponseEntity.ok(orderService.getMyOrder(id, mockUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMyOrder(@PathVariable Long id) {
        // TODO: Récupérer l'utilisateur authentifié depuis le contexte
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");

        orderService.cancelMyOrder(id, mockUser);
        return ResponseEntity.noContent().build();
    }
}
