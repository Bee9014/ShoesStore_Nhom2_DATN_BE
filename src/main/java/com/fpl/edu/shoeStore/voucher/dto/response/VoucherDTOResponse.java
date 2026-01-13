package com.fpl.edu.shoeStore.voucher.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTOResponse {
    private Integer voucherId;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private String description;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer usageLimitPerUser;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- CÁC FIELD MỚI THÊM CHO LOGIC CHECK ---
    private boolean isEligible; // Đủ điều kiện dùng chưa?
    private String reason; // Lý do (VD: "Mua thêm 10k")
    private BigDecimal missingAmount; // Số tiền cần mua thêm
}
