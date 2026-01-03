package com.fpl.edu.shoeStore.product.mapper;

import com.fpl.edu.shoeStore.product.entity.ProductVariant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductVariantMapper {
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);

    ProductVariant findById(@Param("variantId") Integer variantId);

    int insert(ProductVariant variant);

    int update(ProductVariant variant);

    int deleteById(@Param("variantId") Integer variantId);

    List<ProductVariant> findAll();

    int updateStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);

    ProductVariant findByCode(@Param("productVariantCode") String productVariantCode);
}