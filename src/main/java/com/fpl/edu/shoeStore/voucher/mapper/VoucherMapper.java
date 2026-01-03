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
     * Lấy danh sách toàn bộ các mã giảm giá có trong hệ thống.
     */
    List<Voucher> findAll();

    /**
     * Tìm kiếm thông tin chi tiết của một Voucher dựa trên ID duy nhất.
     */
    Voucher findById(Integer id);

    /**
     * Tìm kiếm Voucher theo mã code (Ví dụ: 'SALE2024').
     * Trả về danh sách vì một mã code có thể được thiết lập cho nhiều đợt phát hành khác nhau.
     */
    List<Voucher> findByCode(String code);

    /**
     * Thêm mới một chương trình Voucher vào cơ sở dữ liệu.
     * SQL Server sẽ tự động sinh ID Identity và MyBatis gán ngược vào object voucher.
     */
    int insert(Voucher voucher);

    /**
     * Cập nhật thông tin Voucher (Ví dụ: thay đổi ngày hết hạn, số lượng sử dụng còn lại).
     */
    int update(Voucher voucher);

    /**
     * Xóa vĩnh viễn một Voucher khỏi hệ thống dựa trên ID.
     */
    int deleteById(Integer id);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN) ====================

    /**
     * Tìm kiếm và phân trang danh sách Voucher với nhiều tiêu chí lọc nâng cao.
     * @param offset: Vị trí bắt đầu lấy bản ghi.
     * @param size: Số lượng bản ghi cần lấy.
     * Lưu ý XML: SQL Server sử dụng OFFSET #{offset} ROWS FETCH NEXT #{size} ROWS ONLY.
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
     * Đếm tổng số lượng Voucher thỏa mãn các điều kiện lọc để phục vụ tính toán phân trang.
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