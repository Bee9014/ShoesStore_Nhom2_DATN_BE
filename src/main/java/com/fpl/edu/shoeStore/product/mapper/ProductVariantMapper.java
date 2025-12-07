package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.ProductVariant;  // Add this line

@Mapper
public interface ProductVariantMapper {

    List<ProductVariant>findByProductId(@Param("productId") Long productId);

    void ProductVariantfindById(@Param("variantId") Long variantId);

    int insert(ProductVariant variant);

    int update(ProductVariant variant);

    int deleteById(@Param("variantId") Long variantId);
}


