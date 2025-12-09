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

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
//    private final OrderMapper orderMapper;

    @Override
    public PageResponse<PaymentDTOResponse> findAllPaged(Integer paymentId, Integer orderId, Integer payerId, String paymentMethod, LocalDateTime paymentDate, String status, BigDecimal amount, String transactionCode, int page, int size) {
        int offset = (page - 1) * size;

        List<Payment> payments = paymentMapper.findAllPaged
                (paymentId, orderId, payerId, paymentMethod, paymentDate, status, amount, transactionCode, offset, size);

        long totalElements = paymentMapper.countAll
                (paymentId, orderId, payerId, paymentMethod, paymentDate, status, amount, transactionCode);

        List<PaymentDTOResponse> paymentDTO = PaymentConverter.toDTOList(payments);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<PaymentDTOResponse>builder()
                .content(paymentDTO)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    /**
     *  TẠO THANH TOÁN MỚI
     *  TÁI SỬ DỤNG CODE:
     *  -   Được gọi bởi OrderService khi tạo thanh toán mới.
     *  -   Được gọi bởi PaymentController khi thanh toán lại
     * */
    @Override
    @Transactional
    public PaymentDTOResponse createPayment(PaymentDTORequest request) {

        // VALIDATION
        // Đảm bảo không tạo thanh toán cho đơn hàng "ma" hoặc sai số tiền

//        Order order = orderMapper.findById((long)request.getOrderId());
//        if (order == null) {
//            throw new RuntimeException("Đơn hàng không tồn tại!");
//        }
//        if (request.getAmount().compareTo(order.getTotalPrice()) != 0){
//            throw new RuntimeException("Sai lệch số tiền thanh toán !");
//        }

        Payment payment = PaymentConverter.toEntity(request);

        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        paymentMapper.insert(payment);

        return PaymentConverter.toDTO(payment);
    }

    @Override
    @Transactional
    public PaymentDTOResponse updatePayment(Integer id, PaymentDTORequest request) {
        Payment payment = paymentMapper.findById(id);

        if (payment == null ){
            throw new RuntimeException("Không tìm thấy Payment ID: " + id);
        }

        if (request.getPaymentMethod() != null) payment.setPaymentMethod(request.getPaymentMethod());
        if (request.getAmount() != null) payment.setAmount(request.getAmount());
        if (request.getTransactionCode() != null) payment.setTransactionCode(request.getTransactionCode());

        paymentMapper.update(payment);

        return PaymentConverter.toDTO(payment);
    }

    @Override
    @Transactional
    public int deletePayment(Integer id) {
        Payment payment = paymentMapper.findById(id);
        if (payment == null) {
            throw new RuntimeException("Không tìm thấy Payment ID: "+ id);
        }
        return paymentMapper.deleteById(id);
    }

    @Override
    public PaymentDTOResponse findById(Integer id) {
        Payment payment = paymentMapper.findById(id);
        if (payment == null) {
            throw new RuntimeException("Không tìm thấy Payment ID: "+ id);
        }
        return PaymentConverter.toDTO(payment);
    }

    @Override
    public PaymentDTOResponse findByCode(String transactionCode) {
        Payment payment = paymentMapper.findByCode(transactionCode);
        if (payment == null) {
            throw new RuntimeException("Mã giao dịch không tồn tại!");
        }
        return PaymentConverter.toDTO(payment);
    }
}