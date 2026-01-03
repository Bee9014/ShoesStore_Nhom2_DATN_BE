package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.ProductVariant;

@Mapper
public interface ProductVariantMapper {
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);

    ProductVariant findById(@Param("variantId") Integer variantId);

    int insert(ProductVariant variant);

    int update(ProductVariant variant);

    int deleteById(@Param("variantId") Integer variantId);

    List<ProductVariant> findAll();

    /**
     * Cập nhật số lượng tồn kho. 
     * Thường dùng: SET stock_quantity = stock_quantity + #{quantity}
     */
    int updateStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);

    ProductVariant findByCode(@Param("productVariantCode") String productVariantCode);
    
    /**
     * Thống kê hàng sắp hết (SQL Server: count_low_stock)
     */
    Long countLowStock(@Param("threshold") Integer threshold);
}