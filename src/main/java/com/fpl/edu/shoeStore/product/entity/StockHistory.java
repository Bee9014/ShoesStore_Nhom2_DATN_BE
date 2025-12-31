package com.fpl.edu.shoeStore.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockHistory {
    private Integer historyId;
    private Integer variantId;
    private Integer amount;      // Số lượng thay đổi (+/-)
    private Integer stockBefore; // Tồn trước khi đổi
    private Integer stockAfter;  // Tồn sau khi đổi
    private String note;         // Ghi chú (Nhập hàng, bán hàng...)
    private Integer createBy;    // Người thực hiện
    private LocalDateTime createAt;
}