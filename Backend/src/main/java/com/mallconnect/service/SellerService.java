package com.mallconnect.service;

import com.mallconnect.dto.OrderDto;
import com.mallconnect.dto.ProductDto;
import com.mallconnect.model.Order;
import com.mallconnect.repository.OrderRepository;
import com.mallconnect.repository.ProductRepository;
import com.mallconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {

    private final ProductService productService;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getMyProducts(String sellerEmail, Pageable pageable) {
        return productService.getSellerProducts(sellerEmail, pageable);
    }

    public ProductDto createProduct(String sellerEmail, ProductDto dto) {
        return productService.createProduct(dto, sellerEmail);
    }

    public ProductDto updateProduct(Long productId, ProductDto dto, String sellerEmail) {
        return productService.updateProduct(productId, dto, sellerEmail);
    }

    public void deleteProduct(Long productId, String sellerEmail) {
        productService.deleteProduct(productId, sellerEmail);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getMyOrders(String sellerEmail, Pageable pageable) {
        return orderService.getSellerOrders(sellerEmail, pageable);
    }

    public OrderDto updateOrderStatus(Long orderId, String status, String sellerEmail) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return orderService.updateOrderStatus(orderId, orderStatus, sellerEmail);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard(String sellerEmail) {
        var user = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        long totalProducts = productRepository.findBySellerId(user.getId()).size();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalProducts", totalProducts);
        dashboard.put("sellerName", user.getFirstName() + " " + user.getLastName());
        dashboard.put("sellerEmail", user.getEmail());
        return dashboard;
    }
}
