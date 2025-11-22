package com.mallconnect.service;

import com.mallconnect.dto.OrderDto;
import com.mallconnect.dto.OrderItemDto;
import com.mallconnect.model.*;
import com.mallconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderDto createOrderFromCart(String userEmail, OrderDto orderDto) {
        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(buyer.getId())
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate stock and build order items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName() +
                        ". Available: " + product.getStock());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            orderItems.add(orderItem);

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .buyer(buyer)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(orderDto.getShippingAddress())
                .contactPhone(orderDto.getContactPhone())
                .build();

        // Associate items with order
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        order = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return toDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getBuyerOrders(String userEmail, Pageable pageable) {
        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByBuyerId(buyer.getId(), pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Only buyer, seller (of items), or admin can view
        boolean isBuyer = order.getBuyer().getId().equals(user.getId());
        boolean isSeller = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getSeller().getId().equals(user.getId()));
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isBuyer && !isSeller && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        return toDto(order);
    }

    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus newStatus, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Verify ownership or admin
        boolean isSeller = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getSeller().getId().equals(user.getId()));
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isSeller && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return toDto(order);
    }

    public OrderDto cancelOrder(Long orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getBuyer().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Access denied");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return toDto(order);
    }

    // Seller: get orders containing their products
    @Transactional(readOnly = true)
    public Page<OrderDto> getSellerOrders(String sellerEmail, Pageable pageable) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return orderRepository.findOrdersBySellerId(seller.getId(), pageable).map(this::toDto);
    }

    // Admin: get all orders
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toDto);
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + datePart + "-" + uuidPart;
    }

    public OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                itemDtos.add(OrderItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build());
            }
        }

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName())
                .items(itemDtos)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddress())
                .contactPhone(order.getContactPhone())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
