package com.fpl.edu.shoeStore.payment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.payment.entity.Payment;

@Mapper
public interface PaymentMapper {

    /**
     * Tìm giao dịch thanh toán theo mã tham chiếu nội bộ (Transaction Reference).
     * Dùng để đối soát với phản hồi từ VNPAY/Momo.
     */
    Payment findByTransactionRef(@Param("transactionRef") String transactionRef);
    
    /**
     * Tìm thông tin thanh toán mới nhất của một đơn hàng.
     */
    Payment findByOrderId(@Param("orderId") Integer orderId);

    /**
     * Tạo bản ghi giao dịch thanh toán mới.
     */
    int insert(Payment payment);

    /**
     * Cập nhật trạng thái thanh toán và thông tin từ phía ngân hàng trả về.
     */
    int updatePaymentStatus(
            @Param("transactionRef") String transactionRef,
            @Param("status") String status,
            @Param("gatewayTransactionId") String gatewayTransactionId,
            @Param("bankCode") String bankCode
    );

    /**
     * Phân trang danh sách giao dịch phục vụ đối soát tài chính.
     */
    List<Payment> findAllPaged(
            @Param("status") String status,
            @Param("transactionRef") String transactionRef,
            @Param("offset") int offset,
            @Param("size") int size
    );
}