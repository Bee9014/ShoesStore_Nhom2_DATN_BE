package com.fpl.edu.shoeStore.order.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private int orderItemId;
    private int variantId;
    private String productNameSnapshot;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
