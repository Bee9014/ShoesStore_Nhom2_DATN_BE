package com.fpl.edu.shoeStore.voucher.entity;

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
public class Voucher {
    private Integer voucherId;
    private String code;
    private String discountType; // 'PERCENTAGE' or 'FIXED_AMOUNT'
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

    // Audit fields from SQL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
