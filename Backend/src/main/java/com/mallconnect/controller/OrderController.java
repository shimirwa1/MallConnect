package com.mallconnect.controller;

import com.mallconnect.dto.OrderDto;
import com.mallconnect.dto.UpdateOrderStatusRequest;
import com.mallconnect.model.Order;
import com.mallconnect.service.OrderService;
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

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrderFromCart(userDetails.getUsername(), orderDto));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getBuyerOrders(userDetails.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id, userDetails.getUsername()));
    }
}
