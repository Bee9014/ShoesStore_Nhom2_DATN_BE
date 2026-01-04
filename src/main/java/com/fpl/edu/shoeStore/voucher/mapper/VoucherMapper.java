package com.fpl.edu.shoeStore.voucher.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.voucher.entity.Voucher;

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
     * Dùng để kiểm tra các ràng buộc như giá trị giảm, mức chi tiêu tối thiểu trước khi áp dụng.
     */
    Voucher findById(@Param("id") Integer id);

    /**
     * Tìm kiếm Voucher theo mã code (Ví dụ: 'SALE2024').
     * @return Danh sách các Voucher trùng code (dùng trong trường hợp mã code được tái sử dụng cho các đợt khác nhau).
     */
    List<Voucher> findByCode(@Param("code") String code);

    /**
     * Thêm mới một chương trình khuyến mãi/Voucher. 
     * Tự động lấy ID định danh từ SQL Server sau khi insert thành công.
     */
    int insert(Voucher voucher);

    /**
     * Cập nhật thông tin Voucher. 
     * Thường dùng để thay đổi hạn sử dụng hoặc giảm số lượng 'usage_limit' sau khi người dùng áp dụng mã thành công.
     */
    int update(Voucher voucher);

    /**
     * Xóa vĩnh viễn một Voucher khỏi cơ sở dữ liệu.
     */
    int deleteById(@Param("id") Integer id);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN) ====================

    /**
     * Truy vấn danh sách Voucher với bộ lọc đa năng cho trang quản trị.
     * Hỗ trợ lọc theo thời gian hiệu lực (startDate, endDate) và điều kiện sử dụng (minSpend).
     * @param offset Vị trí bắt đầu lấy dữ liệu (Phục vụ phân trang).
     * @param size Số lượng bản ghi hiển thị trên mỗi trang.
     */
    List<Voucher> findAllPaged(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("description") String description,
            @Param("type") String type,
            @Param("discountValue") BigDecimal discountValue,
            @Param("minSpend") BigDecimal minSpend,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("usageLimit") Integer usageLimit,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Đếm tổng số lượng Voucher thỏa mãn bộ lọc phía trên.
     * Dùng để tính toán tổng số trang trên giao diện quản lý của Admin.
     */
    long countAll(
            @Param("voucherId") Integer voucherId,
            @Param("code") String code,
            @Param("description") String description,
            @Param("type") String type,
            @Param("discountValue") BigDecimal discountValue,
            @Param("minSpend") BigDecimal minSpend,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("usageLimit") Integer usageLimit
    );
}