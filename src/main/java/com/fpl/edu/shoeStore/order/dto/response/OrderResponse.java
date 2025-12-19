package com.fpl.edu.shoeStore.order.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private int orderId;
    private int buyerId;
    private Integer voucherId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount; // Tổng giá trị sản phẩm
    private BigDecimal discountAmount; // Số tiền giảm giá
    private BigDecimal finalAmount; // Tổng tiền phải trả
    private BigDecimal shippingFee;

    private String shippingFullname;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingCountry;
    private String note;

    private List<OrderItemResponse> items;
}
