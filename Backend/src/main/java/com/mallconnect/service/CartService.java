package com.mallconnect.service;

import com.mallconnect.dto.CartDto;
import com.mallconnect.dto.CartItemDto;
import com.mallconnect.model.Cart;
import com.mallconnect.model.CartItem;
import com.mallconnect.model.Product;
import com.mallconnect.model.User;
import com.mallconnect.repository.CartItemRepository;
import com.mallconnect.repository.CartRepository;
import com.mallconnect.repository.ProductRepository;
import com.mallconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartDto getCart(String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        return toDto(cart);
    }

    public CartDto addItem(String userEmail, CartItemDto itemDto) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

        if (product.getStock() < itemDto.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        // Check if item already in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemDto.getQuantity());
            item.setPrice(product.getPrice());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return toDto(cart);
    }

    public CartDto updateItemQuantity(String userEmail, Long itemId, Integer quantity) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to your cart");
        }

        if (item.getProduct().getStock() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + item.getProduct().getStock());
        }

        item.setQuantity(quantity);
        item.setPrice(item.getProduct().getPrice());
        cartItemRepository.save(item);

        return toDto(cart);
    }

    public CartDto removeItem(String userEmail, Long itemId) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to your cart");
        }

        cartItemRepository.delete(item);

        return toDto(cart);
    }

    public void clearCart(String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteAll(cart.getItems());
        cart.setItems(new ArrayList<>());
        cartRepository.save(cart);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    public CartDto toDto(Cart cart) {
        int totalItems = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        java.util.List<CartItemDto> itemDtos = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                itemDtos.add(CartItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(subtotal)
                        .build());
                totalItems += item.getQuantity();
                totalPrice = totalPrice.add(subtotal);
            }
        }

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemDtos)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }
}
