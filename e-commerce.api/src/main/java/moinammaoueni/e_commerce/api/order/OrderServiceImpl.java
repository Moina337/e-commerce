package moinammaoueni.e_commerce.api.order;

import moinammaoueni.e_commerce.api.common.exception.ResourceNotFoundException;
import moinammaoueni.e_commerce.api.order.dto.OrderManagementResponseDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderRequestDTO;
import moinammaoueni.e_commerce.api.order.dto.OrderResponseDTO;
import moinammaoueni.e_commerce.api.order.mapper.OrderMapper;
import moinammaoueni.e_commerce.api.product.Product;
import moinammaoueni.e_commerce.api.product.ProductRepository;
import moinammaoueni.e_commerce.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request, User currentUser) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La liste des articles ne peut pas être vide");
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<Long> productIds = new HashSet<>();

        for (var itemRequest : request.getItems()) {
            Long productId = itemRequest.getProductId();

            if (productIds.contains(productId)) {
                throw new IllegalArgumentException("Le même produit apparaît plusieurs fois dans la commande");
            }
            productIds.add(productId);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + productId));

            int requestedQuantity = itemRequest.getQuantity();

            if (requestedQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour le produit : " + product.getName()
                        + ". Disponible : " + product.getStockQuantity() + ", demandé : " + requestedQuantity);
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(requestedQuantity);
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(requestedQuantity)));

            order.getItems().add(item);
            totalAmount = totalAmount.add(item.getSubtotal());

            // Mise à jour du stock
            product.setStockQuantity(product.getStockQuantity() - requestedQuantity);
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getMyOrders(User currentUser, Pageable pageable) {
        return orderRepository.findByUser(currentUser, pageable)
                .map(orderMapper::toOrderResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getMyOrder(Long orderId, User currentUser) {
        Order order = orderRepository.findByIdAndUser(orderId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable avec l'id : " + orderId));
        return orderMapper.toOrderResponseDTO(order);
    }

    @Override
    @Transactional
    public void cancelMyOrder(Long orderId, User currentUser) {
        Order order = orderRepository.findByIdAndUser(orderId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable avec l'id : " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Une commande livrée ne peut pas être annulée");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("La commande est déjà annulée");
        }

        // Restaurer le stock pour tous les articles
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderManagementResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toOrderManagementResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderManagementResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable avec l'id : " + orderId));
        return orderMapper.toOrderManagementResponseDTO(order);
    }

    @Override
    @Transactional
    public OrderManagementResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable avec l'id : " + orderId));

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toOrderManagementResponseDTO(updatedOrder);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Les règles de transition de statut
        if (currentStatus == newStatus) {
            throw new IllegalStateException("Le nouveau statut doit être différent du statut actuel");
        }

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Une commande livrée ne peut pas être modifiée");
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Une commande annulée ne peut pas être modifiée");
        }

        // Validations spécifiques de transition
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Une commande en attente ne peut être que confirmée ou annulée");
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Une commande confirmée ne peut être préparée ou annulée");
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Une commande en préparation ne peut être expédiée ou annulée");
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("Une commande expédiée ne peut que être livrée");
                }
                break;
            default:
                break;
        }
    }
}
