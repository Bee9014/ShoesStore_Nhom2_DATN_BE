package com.fpl.edu.shoeStore.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Order {
    private int orderId;
    private int userId;
    private int buyerId;
    private Integer voucherId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String shippingFullname;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingCountry;
    private BigDecimal ShippingFee;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
