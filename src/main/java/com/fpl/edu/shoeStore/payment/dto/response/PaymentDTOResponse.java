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
    
    // VNPay fields
    private String transactionRef;          // Mã tham chiếu (vnp_TxnRef)
    private String gatewayTransactionId;    // Mã giao dịch VNPay (vnp_TransactionNo)
    private String bankCode;                // Mã ngân hàng
    private String transactionDesc;         // Mô tả/lỗi giao dịch
    
    private LocalDateTime createdAt;
}
