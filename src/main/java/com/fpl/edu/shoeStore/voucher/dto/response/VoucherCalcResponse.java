package com.fpl.edu.shoeStore.voucher.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherCalcResponse {
    private Integer voucherId;
    private String code;
    private BigDecimal discountAmount; // Số tiền được giảm thực tế
    private boolean isValid;
    private String message; // "Áp dụng thành công" hoặc "Mã hết hạn"
}
