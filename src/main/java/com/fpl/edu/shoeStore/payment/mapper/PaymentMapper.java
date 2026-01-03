package com.fpl.edu.shoeStore.payment.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.payment.entity.Payment;

@Mapper
public interface PaymentMapper {

    List<Payment> findAll();

    Payment findById(@Param("id") Integer id);

    Payment findByTransactionRef(@Param("transactionRef") String transactionRef);
    
    /**
     * Trong SQL Server, câu lệnh này thường dùng TOP 1 để tránh lỗi 
     * nếu một đơn hàng có nhiều lượt thử thanh toán.
     */
    Payment findByOrderId(@Param("orderId") Integer orderId);

    // Trả về int (số dòng bị tác động)
    int insert(Payment payment);

    int update(Payment payment);
    
    /**
     * Cập nhật trạng thái từ Gateway (VNPAY, MoMo,...)
     * Lưu ý trong XML: Sử dụng GETDATE() để ghi nhận thời gian thanh toán.
     */
    int updatePaymentStatus(
            @Param("transactionRef") String transactionRef,
            @Param("status") String status,
            @Param("gatewayTransactionId") String gatewayTransactionId,
            @Param("bankCode") String bankCode
    );

    int deleteById(@Param("id") int id);

    /**
     * Phân trang cho SQL Server.
     * @param offset: Vị trí bắt đầu (thường là pageIndex * pageSize)
     * @param size: Số lượng bản ghi (FETCH NEXT)
     */
    List<Payment> findAllPaged(
            @Param("paymentId") Integer paymentId,
            @Param("orderId") Integer orderId,
            @Param("payerId") Integer payerId,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentDate") LocalDateTime paymentDate,
            @Param("status") String status,
            @Param("amount") BigDecimal amount,
            @Param("transactionRef") String transactionRef,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Đếm tổng số lượng thanh toán.
     * SQL Server COUNT(*) trả về kiểu Long.
     */
    long countAll(
            @Param("paymentId") Integer paymentId,
            @Param("orderId") Integer orderId,
            @Param("payerId") Integer payerId,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentDate") LocalDateTime paymentDate,
            @Param("status") String status,
            @Param("amount") BigDecimal amount,
            @Param("transactionRef") String transactionRef
    );
}