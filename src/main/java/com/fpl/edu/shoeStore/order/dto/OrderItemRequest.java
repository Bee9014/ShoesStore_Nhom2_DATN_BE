package com.fpl.edu.shoeStore.order.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderItemRequest {
    @NotNull(message = "Variant ID is required")
    private Integer variantId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    // unitPrice có thể được tính toán ở tầng Service thay vì nhận từ request
    // private BigDecimal unitPrice;
}