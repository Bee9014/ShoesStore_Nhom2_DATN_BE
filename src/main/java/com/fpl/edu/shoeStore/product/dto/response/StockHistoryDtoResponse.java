package com.fpl.edu.shoeStore.product.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;



import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHistoryDtoResponse {
    private Integer historyId;
    
    // Thông tin biến thể (để hiển thị cho rõ)
    private Integer variantId;
    // private String skuCode; // (Optional) Nếu bạn muốn join bảng để lấy mã SKU
    
    // Thông tin thay đổi
    private Integer amount;      // +10 hoặc -5
    private Integer stockBefore; // Tồn cũ
    private Integer stockAfter;  // Tồn mới
    
    private String note;
    private String createBy;     // Có thể trả về tên Admin thay vì ID
    
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss") // Format ngày giờ cho đẹp
    private LocalDateTime createAt;
}