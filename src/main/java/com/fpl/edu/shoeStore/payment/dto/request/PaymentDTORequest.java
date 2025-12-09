package com.fpl.edu.shoeStore.payment.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTORequest {
    private Integer orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private String transactionCode;
}
