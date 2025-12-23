package com.fpl.edu.shoeStore.payment.controller.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fpl.edu.shoeStore.auth.config.VNPayConfig;
import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.payment.dto.request.PaymentDTORequest;
import com.fpl.edu.shoeStore.payment.dto.response.PaymentDTOResponse;
import com.fpl.edu.shoeStore.payment.service.PaymentService;
import com.fpl.edu.shoeStore.payment.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Payment REST Controller
 * Base URL: /api/v1/payments
 * * Note: Các lỗi (Exception) sẽ được GlobalExceptionHandler bắt và xử lý.
 * Controller chỉ tập trung vào luồng nghiệp vụ thành công (Happy Path).
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VNPayService vnPayService;

    // --- GET ALL ---
    @GetMapping
    public ApiResponse<PageResponse<PaymentDTOResponse>> getAllPayments(
            @RequestParam(required = false) Integer paymentId,
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) Integer payerId,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paymentDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String transactionRef,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PaymentDTOResponse> pageResponse = paymentService.findAllPaged(
                paymentId, orderId, payerId, paymentMethod,
                paymentDate, status, amount, transactionRef,
                page, size
        );

        return ApiResponse.<PageResponse<PaymentDTOResponse>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Lấy danh sách payment thành công")
                .data(pageResponse)
                .build();
    }

    // --- GET BY ID ---
    @GetMapping("/{id}")
    public ApiResponse<PaymentDTOResponse> getPaymentById(@PathVariable Integer id) {
        PaymentDTOResponse payment = paymentService.findById(id);
        
        // Nếu Service trả về null mà chưa ném exception, ta ném tại đây
        if (payment == null) {
            throw new RuntimeException("Không tìm thấy payment với ID: " + id);
        }

        return ApiResponse.<PaymentDTOResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Lấy thông tin payment thành công")
                .data(payment)
                .build();
    }

    // --- CREATE PAYMENT (Internal/COD) ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Đánh dấu HTTP 201 Created
    public ApiResponse<PaymentDTOResponse> createPayment(@RequestBody PaymentDTORequest request) {
        PaymentDTOResponse created = paymentService.createPayment(request);

        return ApiResponse.<PaymentDTOResponse>builder()
                .success(true)
                .statusCode(HttpStatus.CREATED.value())
                .message("Tạo payment thành công")
                .data(created)
                .build();
    }

    // --- CREATE VNPAY URL ---
    @PostMapping("/vnpay/create-payment")
    public ApiResponse<Map<String, String>> createVNPayPayment(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request
    ) {
        Integer orderId = (Integer) requestData.get("orderId");
        Integer amount = (Integer) requestData.get("amount");
        Integer payerId = (Integer) requestData.get("payerId");
        String orderInfo = (String) requestData.getOrDefault("orderInfo", "Thanh toán đơn hàng #" + orderId);
        String returnUrl = (String) requestData.getOrDefault("returnUrl", VNPayConfig.vnp_ReturnUrl);

        // Validation: Ném lỗi để GlobalHandler bắt (sẽ trả về 400 Bad Request)
        if (orderId == null || amount == null || amount <= 0) {
            throw new IllegalArgumentException("orderId và amount không hợp lệ");
        }

        // 1. Tạo bản ghi Payment PENDING
        String transactionRef = "ORD" + orderId + "_" + System.currentTimeMillis();
        PaymentDTORequest paymentRequest = PaymentDTORequest.builder()
                .orderId(orderId)
                .paymentMethod("VNPAY")
                .amount(BigDecimal.valueOf(amount))
                .payerId(payerId)
                .transactionRef(transactionRef)
                .build();
        
        PaymentDTOResponse payment = paymentService.createPayment(paymentRequest);

        // 2. Gọi VNPay Service lấy URL
        String vnpayUrl = vnPayService.createVnPayUrl(amount, orderInfo, returnUrl, transactionRef);

        // 3. Trả về kết quả
        Map<String, String> responseData = new HashMap<>();
        responseData.put("paymentUrl", vnpayUrl);
        responseData.put("transactionRef", transactionRef);
        responseData.put("paymentId", payment.getPaymentId().toString());

        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Tạo URL thanh toán VNPay thành công")
                .data(responseData)
                .build();
    }

    // --- VNPAY CALLBACK ---
    @GetMapping("/vnpay/callback")
    public ApiResponse<Map<String, Object>> vnpayCallback(@RequestParam Map<String, String> vnpayParams) {
        String vnp_ResponseCode = vnpayParams.get("vnp_ResponseCode");
        String vnp_TxnRef = vnpayParams.get("vnp_TxnRef");
        String vnp_TransactionNo = vnpayParams.get("vnp_TransactionNo");
        String vnp_BankCode = vnpayParams.get("vnp_BankCode");

        // Logic xử lý status
        boolean isSuccess = "00".equals(vnp_ResponseCode);
        String status = isSuccess ? "PAID" : "FAILED";
        String msg = isSuccess ? "Thanh toán thành công" : "Thanh toán thất bại";

        // Gọi service update (Nếu có lỗi DB, nó sẽ tự throw exception)
        paymentService.updatePaymentStatus(vnp_TxnRef, status, vnp_TransactionNo, vnp_BankCode);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", isSuccess ? "SUCCESS" : "FAILED");
        responseData.put("transactionRef", vnp_TxnRef);
        responseData.put("gatewayTransactionId", vnp_TransactionNo);

        return ApiResponse.<Map<String, Object>>builder()
                .success(isSuccess)
                .statusCode(HttpStatus.OK.value())
                .message(msg)
                .data(responseData)
                .build();
    }

    // --- UPDATE PAYMENT ---
    @PutMapping("/{id}")
    public ApiResponse<PaymentDTOResponse> updatePayment(
            @PathVariable Integer id,
            @RequestBody PaymentDTORequest request
    ) {
        // Service nên throw Exception nếu không tìm thấy ID
        PaymentDTOResponse updated = paymentService.updatePayment(id, request);
        
        return ApiResponse.<PaymentDTOResponse>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật payment thành công")
                .data(updated)
                .build();
    }

    // --- DELETE PAYMENT ---
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePayment(@PathVariable Integer id) {
        // Service nên throw Exception nếu không tìm thấy ID
        paymentService.deletePayment(id);
        
        return ApiResponse.<Void>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Xóa payment thành công")
                .data(null)
                .build();
    }
}