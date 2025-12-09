package com.fpl.edu.shoeStore.payment.convert;


import com.fpl.edu.shoeStore.payment.dto.request.PaymentDTORequest;
import com.fpl.edu.shoeStore.payment.dto.response.PaymentDTOResponse;
import com.fpl.edu.shoeStore.payment.entity.Payment;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentConverter {
    public static Payment toEntity(PaymentDTORequest dto) {
        if (dto == null) return null;

        Payment payment = new Payment();

        payment.setOrderId(dto.getOrderId());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setAmount(dto.getAmount());
        payment.setTransactionCode(dto.getTransactionCode());

        return payment;
    }

    public static PaymentDTOResponse toDTO(Payment payment) {
        if (payment == null)     return null;

        return PaymentDTOResponse.builder()
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .payerId(payment.getPayerId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionCode(payment.getTransactionCode())
                .createdAt(payment.getCreatedAt())

                .build();
    }

    public static List<PaymentDTOResponse> toDTOList(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return List.of();

        return payments.stream()
                .map(PaymentConverter::toDTO)
                .collect(Collectors.toList());
    }
}