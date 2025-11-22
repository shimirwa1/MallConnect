package com.mallconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;
    private String productImageUrl;

    @NotNull
    @Min(1)
    private Integer quantity;

    private BigDecimal price;
    private BigDecimal subtotal;
}
