package com.fpl.edu.shoeStore.voucher.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTORequest {
    private Integer voucherId;
    private String code;
    private String description;
    private String type;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
}
