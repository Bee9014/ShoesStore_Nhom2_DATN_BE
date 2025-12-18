package com.fpl.edu.shoeStore.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull(message = "Cần có ID đơn hàng")
    private Integer variantId;

    @Min(value = 1, message = "Số lượng phải tối thiểu là 1")
    private int quantity;

    // unitPrice có thể được tính toán ở tầng Service thay vì nhận từ request
    // private BigDecimal unitPrice;
}