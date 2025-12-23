package com.fpl.edu.shoeStore.payment.convert;

import java.util.List;
import java.util.stream.Collectors;

import com.fpl.edu.shoeStore.payment.dto.request.PaymentDTORequest;
import com.fpl.edu.shoeStore.payment.dto.response.PaymentDTOResponse;
import com.fpl.edu.shoeStore.payment.entity.Payment;

public class PaymentConverter {
    public static Payment toEntity(PaymentDTORequest dto) {
        if (dto == null) return null;

        Payment payment = new Payment();
        
        payment.setOrderId(dto.getOrderId());
        payment.setPayerId(dto.getPayerId()); // Map thêm payerId
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setAmount(dto.getAmount());
        
        // Nếu DTO có truyền status thì set, không thì thôi (để Service xử lý default)
        if (dto.getStatus() != null) {
            payment.setStatus(dto.getStatus());
        }

        // VNPay fields
        payment.setTransactionRef(dto.getTransactionRef());
        payment.setGatewayTransactionId(dto.getGatewayTransactionId());
        payment.setBankCode(dto.getBankCode());
        payment.setTransactionDesc(dto.getTransactionDesc());

        return payment;
    }

    public static PaymentDTOResponse toDTO(Payment payment) {
        if (payment == null) return null;

        return PaymentDTOResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .payerId(payment.getPayerId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                
                // VNPay fields
                .transactionRef(payment.getTransactionRef())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .bankCode(payment.getBankCode())
                .transactionDesc(payment.getTransactionDesc())
                
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public static List<PaymentDTOResponse> toDTOList(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return List.of();
        return payments.stream().map(PaymentConverter::toDTO).collect(Collectors.toList());
    }
}