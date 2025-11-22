package com.mallconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @Min(0)
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    private String imageUrl;

    private boolean active;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    private Long sellerId;
    private String sellerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
