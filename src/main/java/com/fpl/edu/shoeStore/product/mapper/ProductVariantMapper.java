package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.ProductVariant;

@Mapper
public interface ProductVariantMapper {

    /**
     * Lấy danh sách tất cả các biến thể thuộc về một sản phẩm cụ thể.
     * Ví dụ: Lấy tất cả các size và màu của đôi "Nike Air Force 1".
     */
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);

    /**
     * Tìm kiếm thông tin chi tiết của một biến thể dựa trên mã ID duy nhất.
     */
    ProductVariant findById(@Param("variantId") Integer variantId);

    /**
     * Thêm mới một biến thể sản phẩm (Size/Màu mới).
     * SQL Server sẽ tự động sinh ID (Identity) và MyBatis gán ngược vào object.
     */
    int insert(ProductVariant variant);

    /**
     * Cập nhật thông tin biến thể (Thay đổi giá, màu sắc hoặc thuộc tính khác).
     */
    int update(ProductVariant variant);

    /**
     * Xóa vĩnh viễn một biến thể khỏi hệ thống dựa trên ID.
     */
    int deleteById(@Param("variantId") Integer variantId);

    /**
     * Lấy danh sách toàn bộ các biến thể của tất cả sản phẩm trong hệ thống.
     */
    List<ProductVariant> findAll();

    /**
     * Cập nhật số lượng tồn kho của biến thể.
     * @param variantId: ID của biến thể cần cập nhật.
     * @param quantity: Số lượng thay đổi (dùng số dương để nhập kho, số âm để xuất kho).
     * Lưu ý XML: SET stock_quantity = stock_quantity + #{quantity}
     */
    int updateStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);

    /**
     * Tìm kiếm biến thể bằng mã Code duy nhất (SKU).
     * Thường dùng để quét mã vạch hoặc tìm nhanh sản phẩm trong kho.
     */
    ProductVariant findByCode(@Param("productVariantCode") String productVariantCode);
    
    /**
     * Thống kê số lượng biến thể có mức tồn kho dưới ngưỡng cho phép.
     * Phục vụ chức năng cảnh báo hết hàng trên Dashboard Admin.
     * @param threshold: Ngưỡng số lượng (Ví dụ: dưới 5 sản phẩm là sắp hết).
     */
    Long countLowStock(@Param("threshold") Integer threshold);
}