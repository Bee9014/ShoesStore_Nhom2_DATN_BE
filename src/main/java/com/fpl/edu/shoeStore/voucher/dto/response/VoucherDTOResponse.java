package com.fpl.edu.shoeStore.voucher.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VoucherDTOResponse {

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
