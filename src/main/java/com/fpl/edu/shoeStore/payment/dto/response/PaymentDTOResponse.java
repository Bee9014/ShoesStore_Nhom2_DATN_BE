package com.fpl.edu.shoeStore.payment.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentDTOResponse {
    private Integer paymentId;
    private Integer orderId;
    private Integer payerId;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String status;
    private String transactionCode;
    private LocalDateTime createdAt;
}
