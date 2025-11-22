package com.mallconnect.controller;

import com.mallconnect.dto.CartDto;
import com.mallconnect.dto.CartItemDto;
import com.mallconnect.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartItemDto itemDto) {
        return ResponseEntity.ok(cartService.addItem(userDetails.getUsername(), itemDto));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto> updateItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userDetails.getUsername(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUsername(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
