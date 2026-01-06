package com.fpl.edu.shoeStore.voucher.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.voucher.entity.Voucher;
import org.springframework.security.core.parameters.P;

@Mapper
public interface VoucherMapper {

    // ==================== CƠ BẢN (CRUD) ====================

    /**
     * Truy vấn toàn bộ danh sách Voucher. 
     * Thường dùng cho các báo cáo tổng hợp hoặc danh sách nội bộ.
     */
    List<Voucher> findAll();

    /**
     * Tìm thông tin chi tiết của một Voucher qua ID. 
     * Lưu ý: XML mapping tham số là #{id}.
     */
    Voucher findById(@Param("id") Integer id);

    /**
     * Tìm kiếm Voucher theo mã code.
     * XML sử dụng LIKE nên sẽ trả về danh sách các voucher chứa chuỗi code tương ứng.
     */
    List<Voucher> findByCode(@Param("code") String code);

    /**
     * Thêm mới một chương trình khuyến mãi/Voucher. 
     * ID tự tăng (voucherId) sẽ được gán lại vào object Voucher.
     */
    int insert(Voucher voucher);

    /**
     * Cập nhật thông tin Voucher. 
     * Sử dụng voucher_id = #{voucherId} làm điều kiện định danh.
     */
    int update(Voucher voucher);

    /**
     * Xóa vĩnh viễn một Voucher khỏi cơ sở dữ liệu qua ID.
     */
    int deleteById(@Param("id") Integer id);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN) ====================

    /**
     * Truy vấn danh sách Voucher với bộ lọc dựa trên XML hiện tại.
     * Lưu ý: Tên tham số đã được đổi từ 'type' sang 'voucherType' để khớp với #{voucherType} trong XML.
     * @param offset Vị trí bắt đầu (Phân trang).
     * @param size Số lượng bản ghi mỗi trang.
     */
    List<Voucher> findAllPaged(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("description") String description,
            @Param("voucherType") String voucherType,
            @Param("discountValue") BigDecimal discountValue,
            @Param("minSpend") BigDecimal minSpend,
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("usageLimit") Integer usageLimit,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Đếm tổng số lượng Voucher thỏa mãn bộ lọc hiện có trong XML.
     * Phục vụ tính toán tổng số trang cho Admin.
     */
    long countAll(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("description") String description,
            @Param("voucherType") String voucherType,
            @Param("discountValue") BigDecimal discountValue,
            @Param("minSpend") BigDecimal minSpend,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("usageLimit") Integer usageLimit

    );
}