package com.fpl.edu.shoeStore.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull(message = "Mã người mua không được để trống.")
    private Integer buyerId;

    private Integer voucherId; // Có thể null

    // Thông tin vận chuyển
    @NotBlank(message = "Cần cung cấp đầy đủ thông tin người nhận hàng.")
    private String shippingFullname;

    @NotBlank(message = "Cần có số điện thoại để giao hàng.")
    private String shippingPhone;

    @NotBlank(message = "Địa chỉ giao hàng là bắt buộc")
    private String shippingAddress;

    private String shippingCity;
    private String shippingCountry;
    private String note;

    @Size(min = 1, message = "Order must contain at least one item")
    private List<OrderItemRequest> items;

    // Phí vận chuyển và các trường tiền tệ (được tính toán hoặc cung cấp)
    private BigDecimal shippingFee;
}
