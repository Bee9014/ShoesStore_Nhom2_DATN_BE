package com.fpl.edu.shoeStore.voucher.dto.request;

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
public class VoucherDTORequest {
    private Integer voucherId;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private String description;
    private Integer usageLimit;
    private Integer usageLimitPerUser;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
