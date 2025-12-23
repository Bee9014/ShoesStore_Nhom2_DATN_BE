package com.fpl.edu.shoeStore.payment.service.impl;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.payment.convert.PaymentConverter;
import com.fpl.edu.shoeStore.payment.dto.request.PaymentDTORequest;
import com.fpl.edu.shoeStore.payment.dto.response.PaymentDTOResponse;
import com.fpl.edu.shoeStore.payment.entity.Payment;
import com.fpl.edu.shoeStore.payment.mapper.PaymentMapper;
import com.fpl.edu.shoeStore.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;

    @Override
    public PageResponse<PaymentDTOResponse> findAllPaged(
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
    ) {
        int offset = (page - 1) * size;

        List<Payment> payments = paymentMapper.findAllPaged(
                paymentId, orderId, payerId, paymentMethod,
                paymentDate, status, amount, transactionRef,
                offset, size
        );

        long totalElements = paymentMapper.countAll(
                paymentId, orderId, payerId, paymentMethod,
                paymentDate, status, amount, transactionRef
        );

        List<PaymentDTOResponse> dtoList = payments.stream()
                .map(PaymentConverter::toDTO)
                .collect(Collectors.toList());

        return PageResponse.<PaymentDTOResponse>builder()
                .content(dtoList)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .build();
    }

    @Override
    @Transactional
    public PaymentDTOResponse createPayment(PaymentDTORequest request) {
        Payment payment = PaymentConverter.toEntity(request);
        
        // Set default status nếu chưa có
        if (payment.getStatus() == null || payment.getStatus().isEmpty()) {
            payment.setStatus("PENDING");
        }

        paymentMapper.insert(payment);
        return PaymentConverter.toDTO(payment);
    }

    @Override
    @Transactional
    public PaymentDTOResponse updatePayment(Integer id, PaymentDTORequest request) {
        Payment existing = paymentMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Payment không tồn tại với id: " + id);
        }

        // Update fields
        existing.setOrderId(request.getOrderId());
        existing.setPaymentMethod(request.getPaymentMethod());
        existing.setAmount(request.getAmount());
        existing.setTransactionRef(request.getTransactionRef());
        existing.setBankCode(request.getBankCode());
        existing.setTransactionDesc(request.getTransactionDesc());

        paymentMapper.update(existing);
        return PaymentConverter.toDTO(existing);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(
            String transactionRef,
            String status,
            String gatewayTransactionId,
            String bankCode
    ) {
        int updated = paymentMapper.updatePaymentStatus(
                transactionRef,
                status,
                gatewayTransactionId,
                bankCode
        );

        if (updated == 0) {
            throw new RuntimeException("Không tìm thấy payment với transactionRef: " + transactionRef);
        }
    }

    @Override
    @Transactional
    public int deletePayment(Integer id) {
        Payment existing = paymentMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Payment không tồn tại với id: " + id);
        }

        return paymentMapper.deleteById(id);
    }

    @Override
    public PaymentDTOResponse findById(Integer id) {
        Payment payment = paymentMapper.findById(id);
        if (payment == null) {
            return null;
        }
        return PaymentConverter.toDTO(payment);
    }

    @Override
    public PaymentDTOResponse findByTransactionRef(String transactionRef) {
        Payment payment = paymentMapper.findByTransactionRef(transactionRef);
        if (payment == null) {
            return null;
        }
        return PaymentConverter.toDTO(payment);
    }
}
