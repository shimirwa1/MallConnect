package com.mallconnect.controller;

import com.mallconnect.dto.OrderDto;
import com.mallconnect.dto.ProductDto;
import com.mallconnect.dto.UpdateOrderStatusRequest;
import com.mallconnect.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(sellerService.getDashboard(userDetails.getUsername()));
    }

    // --- Product CRUD ---

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getMyProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(sellerService.getMyProducts(userDetails.getUsername(), pageable));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerService.createProduct(userDetails.getUsername(), productDto));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(sellerService.updateProduct(id, productDto, userDetails.getUsername()));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        sellerService.deleteProduct(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // --- Orders ---

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(sellerService.getMyOrders(userDetails.getUsername(), pageable));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(sellerService.updateOrderStatus(id, request.getStatus(), userDetails.getUsername()));
    }
}
