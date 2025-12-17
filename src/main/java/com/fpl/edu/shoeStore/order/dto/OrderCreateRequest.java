package com.fpl.edu.shoeStore.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class OrderCreateRequest {
    @NotNull(message = "Buyer ID must not be null")
    private Integer buyerId;

    private Integer voucherId; // Có thể null

    // Thông tin vận chuyển
    @NotBlank(message = "Shipping Fullname is required")
    private String shippingFullname;

    @NotBlank(message = "Shipping Phone is required")
    private String shippingPhone;

    @NotBlank(message = "Shipping Address is required")
    private String shippingAddress;

    private String shippingCity;
    private String shippingCountry;
    private String note;

    @Size(min = 1, message = "Order must contain at least one item")
    private List<OrderItemRequest> items;

    // Phí vận chuyển và các trường tiền tệ (được tính toán hoặc cung cấp)
    private BigDecimal shippingFee;
}
