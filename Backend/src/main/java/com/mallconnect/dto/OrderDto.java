package com.mallconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private String orderNumber;
    private Long buyerId;
    private String buyerName;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String status;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500)
    private String shippingAddress;

    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
