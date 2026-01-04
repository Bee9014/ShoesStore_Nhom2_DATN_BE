package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.ProductVariant;

@Mapper
public interface ProductVariantMapper {

    /**
     * Lấy danh sách tất cả các biến thể thuộc về một sản phẩm cụ thể.
     * @param productId ID của sản phẩm cha.
     */
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);

    /**
     * Tìm kiếm thông tin chi tiết của một biến thể dựa trên ID duy nhất.
     */
    ProductVariant findById(@Param("variantId") Integer variantId);

    /**
     * Thêm mới một biến thể sản phẩm. 
     * ID tự tăng từ SQL Server sẽ được gán vào trường variantId của object.
     */
    int insert(ProductVariant variant);

    /**
     * Cập nhật toàn bộ thông tin biến thể.
     */
    int update(ProductVariant variant);

    /**
     * Xóa vĩnh viễn một biến thể khỏi hệ thống dựa trên ID.
     */
    int deleteById(@Param("variantId") Integer variantId);

    /**
     * Lấy danh sách toàn bộ các biến thể hiện có trong hệ thống.
     */
    List<ProductVariant> findAll();

    /**
     * Cập nhật số lượng tồn kho (Stock Quantity).
     * @param variantId ID biến thể.
     * @param quantity Số lượng thay đổi (cộng thêm hoặc trừ đi).
     */
    int updateStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);

    /**
     * Tìm kiếm biến thể bằng mã SKU (product_variant_code).
     */
    ProductVariant findByCode(@Param("productVariantCode") String productVariantCode);

    /**
     * LƯU Ý: Phương thức countLowStock hiện chưa có trong file XML bạn cung cấp.
     * Nếu bạn muốn sử dụng, hãy thêm thẻ <select id="countLowStock"> vào XML.
     */
    // Long countLowStock(@Param("threshold") Integer threshold);
}