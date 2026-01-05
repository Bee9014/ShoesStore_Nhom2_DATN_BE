package com.fpl.edu.shoeStore.payment.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.payment.entity.Payment;

@Mapper
public interface PaymentMapper {

    /**
     * Lấy toàn bộ danh sách giao dịch, sắp xếp theo thời gian tạo mới nhất.
     */
    List<Payment> findAll();

    /**
     * Tìm thông tin giao dịch theo ID chính xác.
     */
    Payment findById(@Param("id") Integer id);

    /**
     * Tìm giao dịch theo mã tham chiếu nội bộ (Transaction Reference).
     * Cực kỳ quan trọng để đối soát dữ liệu khi nhận IPN từ VNPAY/Momo.
     */
    Payment findByTransactionRef(@Param("transactionRef") String transactionRef);
    
    /**
     * Tìm thông tin thanh toán mới nhất gắn với một đơn hàng.
     */
    Payment findByOrderId(@Param("orderId") Integer orderId);

    /**
     * Tạo bản ghi giao dịch thanh toán mới. ID sẽ tự động gán vào object.
     */
    int insert(Payment payment);

    /**
     * Cập nhật toàn bộ thông tin của một bản ghi thanh toán.
     */
    int update(Payment payment);

    /**
     * Cập nhật trạng thái thanh toán và thông tin từ ngân hàng sau khi giao dịch hoàn tất.
     */
    int updatePaymentStatus(
            @Param("transactionRef") String transactionRef,
            @Param("status") String status,
            @Param("gatewayTransactionId") String gatewayTransactionId,
            @Param("bankCode") String bankCode
    );

    /**
     * Truy vấn nâng cao: Tìm kiếm, lọc và phân trang danh sách giao dịch.
     * Hỗ trợ lọc theo nhiều tiêu chí: ID, OrderId, PayerId, Method, Status, v.v.
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
     * Đếm tổng số giao dịch dựa trên các tiêu chí lọc để phục vụ phân trang.
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

    /**
     * Xóa bản ghi thanh toán theo ID.
     */
    int deleteById(@Param("id") Integer id);
}