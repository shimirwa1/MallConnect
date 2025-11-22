package com.mallconnect.service;

import com.mallconnect.dto.*;
import com.mallconnect.model.Role;
import com.mallconnect.model.User;
import com.mallconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    // --- User Management ---

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toUserDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toUserDto(user);
    }

    public UserDto updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setRole(Role.valueOf(role));
        user = userRepository.save(user);
        return toUserDto(user);
    }

    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    // --- Product Management ---

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productService::toDto);
    }

    public ProductDto updateProduct(Long id, ProductDto dto) {
        return productService.adminUpdateProduct(id, dto);
    }

    public void deleteProduct(Long id) {
        productService.adminDeleteProduct(id);
    }

    // --- Category Management ---

    public CategoryDto createCategory(CategoryDto dto) {
        return categoryService.createCategory(dto);
    }

    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        return categoryService.updateCategory(id, dto);
    }

    public void deleteCategory(Long id) {
        categoryService.deleteCategory(id);
    }

    // --- Order Management ---

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    // --- Dashboard ---

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long buyers = userRepository.countByRole(Role.ROLE_BUYER);
        long sellers = userRepository.countByRole(Role.ROLE_SELLER);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalUsers", totalUsers);
        dashboard.put("totalProducts", totalProducts);
        dashboard.put("totalOrders", totalOrders);
        dashboard.put("totalBuyers", buyers);
        dashboard.put("totalSellers", sellers);
        return dashboard;
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();
    }
}
