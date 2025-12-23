package com.fpl.edu.shoeStore.order.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int variantId;
    private String productNameSnapshot;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
