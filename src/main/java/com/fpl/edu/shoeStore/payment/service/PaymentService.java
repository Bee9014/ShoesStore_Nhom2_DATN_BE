package com.fpl.edu.shoeStore.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.payment.dto.request.PaymentDTORequest;
import com.fpl.edu.shoeStore.payment.dto.response.PaymentDTOResponse;

public interface PaymentService {

    // SỬA: transactionCode -> transactionRef để khớp DB mới
    PageResponse<PaymentDTOResponse> findAllPaged(
            Integer paymentId,
            Integer orderId,
            Integer payerId,
            String paymentMethod,
            LocalDateTime paymentDate,
            String status,
            BigDecimal amount,
            String transactionRef, 
            int page,
            int size
    );

    PaymentDTOResponse createPayment(PaymentDTORequest request);

    PaymentDTOResponse updatePayment(Integer id, PaymentDTORequest request);
    
    // THÊM: Hàm chuyên dụng để update kết quả từ VNPay/MoMo
    void updatePaymentStatus(String transactionRef, String status, String gatewayTransactionId, String bankCode);

    int deletePayment(Integer id);

    PaymentDTOResponse findById(Integer id);

    PaymentDTOResponse findByTransactionRef(String transactionRef);
}