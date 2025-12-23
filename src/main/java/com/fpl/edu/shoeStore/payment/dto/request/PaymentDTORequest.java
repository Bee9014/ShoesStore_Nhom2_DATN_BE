package com.fpl.edu.shoeStore.payment.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTORequest {
    private Integer orderId;
    private Integer payerId; // Thêm payerId nếu cần lưu người trả tiền
    private String paymentMethod;
    private BigDecimal amount;
    
    // Thêm status để dùng khi update (VD: PENDING -> PAID)
    private String status; 
    
    // Các trường VNPay
    private String transactionRef;          // Map với vnp_TxnRef
    private String gatewayTransactionId;    // Map với vnp_TransactionNo (Thêm mới)
    private String bankCode;                
    private String transactionDesc;         
}