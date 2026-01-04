package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.ProductVariant;

@Mapper
public interface ProductVariantMapper {

    /**
     * Lấy danh sách tất cả các biến thể thuộc về một sản phẩm cụ thể.
     * Thường dùng để hiển thị các tùy chọn Size/Màu trên trang chi tiết sản phẩm.
     * @param productId ID của sản phẩm cha.
     */
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);

    /**
     * Tìm kiếm thông tin chi tiết của một biến thể dựa trên ID duy nhất.
     * Dùng để xác định chính xác thuộc tính sản phẩm khi người dùng chọn vào giỏ hàng.
     */
    ProductVariant findById(@Param("variantId") Integer variantId);

    /**
     * Thêm mới một biến thể sản phẩm (như thêm Size 42 cho mẫu giày hiện có).
     * SQL Server sẽ tự động sinh ID và MyBatis gán ngược vào đối tượng variant.
     */
    int insert(ProductVariant variant);

    /**
     * Cập nhật thông tin biến thể (Thay đổi giá riêng cho từng Size, thay đổi màu sắc, v.v.).
     */
    int update(ProductVariant variant);

    /**
     * Xóa vĩnh viễn một biến thể khỏi hệ thống dựa trên ID.
     */
    int deleteById(@Param("variantId") Integer variantId);

    /**
     * Lấy danh sách toàn bộ các biến thể của tất cả sản phẩm trong hệ thống.
     * Phù hợp cho việc kiểm kê kho hàng tổng thể.
     */
    List<ProductVariant> findAll();

    /**
     * Cập nhật số lượng tồn kho của biến thể một cách an toàn.
     * @param variantId ID của biến thể cần thay đổi số lượng.
     * @param quantity Số lượng cộng thêm hoặc trừ đi (Ví dụ: -1 khi khách mua hàng, +10 khi nhập kho).
     * Logic XML: SET stock_quantity = stock_quantity + #{quantity}
     */
    int updateStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);

    /**
     * Tìm kiếm biến thể bằng mã SKU (Stock Keeping Unit).
     * SKU thường là mã định danh duy nhất cho từng cặp Sản phẩm-Size-Màu để quản lý kho chính xác.
     */
    ProductVariant findByCode(@Param("productVariantCode") String productVariantCode);
    
    /**
     * Thống kê số lượng các mẫu giày/size đang sắp hết hàng.
     * @param threshold Ngưỡng tối thiểu (Ví dụ: Lấy các mẫu có số lượng < 5).
     * Phục vụ chức năng cảnh báo nhập hàng cho quản trị viên.
     */
    Long countLowStock(@Param("threshold") Integer threshold);
}