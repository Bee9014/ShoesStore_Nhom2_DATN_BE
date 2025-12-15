package com.fpl.edu.shoeStore.voucher.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Voucher {
    private Integer voucherId;
    private String code;
    private String description;
    private String type;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
