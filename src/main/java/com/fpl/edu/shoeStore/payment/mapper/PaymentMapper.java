package com.fpl.edu.shoeStore.payment.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.payment.entity.Payment;

@Mapper
public interface PaymentMapper {

    /**
     * Lấy toàn bộ lịch sử giao dịch thanh toán trong hệ thống.
     */
    List<Payment> findAll();

    /**
     * Tìm kiếm thông tin giao dịch dựa trên mã ID nội bộ.
     */
    Payment findById(@Param("id") Integer id);

    /**
     * Tìm kiếm giao dịch dựa trên mã tham chiếu (Transaction Reference).
     * Mã này thường được tạo ra khi bắt đầu gửi yêu cầu thanh toán sang cổng (VNPAY/Momo).
     */
    Payment findByTransactionRef(@Param("transactionRef") String transactionRef);
    
    /**
     * Tìm kiếm thông tin thanh toán dựa trên mã đơn hàng.
     * Lưu ý: Trong SQL Server nên dùng TOP 1 để lấy giao dịch mới nhất nếu đơn hàng có nhiều lần thanh toán lỗi.
     */
    Payment findByOrderId(@Param("orderId") Integer orderId);

    /**
     * Tạo mới một bản ghi giao dịch (thường ở trạng thái 'PENDING').
     * Trả về số dòng được chèn thành công (1).
     */
    int insert(Payment payment);

    /**
     * Cập nhật thông tin giao dịch tổng quát.
     */
    int update(Payment payment);
    
    /**
     * Cập nhật kết quả thanh toán từ cổng thanh toán ngoại vi (VNPAY, MoMo,...) trả về.
     * @param transactionRef: Mã tham chiếu nội bộ.
     * @param status: Trạng thái mới (SUCCESS/FAILED).
     * @param gatewayTransactionId: Mã giao dịch của phía Ngân hàng/Cổng thanh toán.
     * @param bankCode: Mã ngân hàng thực hiện giao dịch.
     */
    int updatePaymentStatus(
            @Param("transactionRef") String transactionRef,
            @Param("status") String status,
            @Param("gatewayTransactionId") String gatewayTransactionId,
            @Param("bankCode") String bankCode
    );

    /**
     * Xóa bản ghi thanh toán theo ID (Hạn chế sử dụng, thường chỉ dùng cho test).
     */
    int deleteById(@Param("id") int id);

    /**
     * Tìm kiếm nâng cao và phân trang danh sách thanh toán.
     * Phục vụ trang quản lý tài chính cho Admin để đối soát doanh thu.
     * Sử dụng cú pháp OFFSET...FETCH NEXT cho SQL Server.
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
     * Đếm tổng số lượng bản ghi thanh toán thỏa mãn bộ lọc để phục vụ phân trang.
     * Kết quả trả về kiểu long tương ứng với BIGINT trong SQL Server.
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