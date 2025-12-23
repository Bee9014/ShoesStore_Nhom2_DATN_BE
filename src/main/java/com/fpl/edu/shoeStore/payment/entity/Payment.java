package com.fpl.edu.shoeStore.payment.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Payment {
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
    private String bankCode;                // Mã ngân hàng (NCB, VCB...)
    private String transactionDesc;         // Mô tả/lỗi giao dịch
    
    private LocalDateTime createdAt;

}