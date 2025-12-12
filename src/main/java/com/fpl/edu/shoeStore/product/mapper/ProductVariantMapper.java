 package com.fpl.edu.shoeStore.product.mapper;

     import com.fpl.edu.shoeStore.product.entity.ProductVariant;
     import org.apache.ibatis.annotations.Mapper;
     import org.apache.ibatis.annotations.Param;

     import java.util.List;

     @Mapper
     public interface ProductVariantMapper {
         List<ProductVariant> findByProductId(@Param("productId") Integer productId);  // Đổi Long → Integer

         ProductVariant findById(@Param("variantId") Integer variantId);                // Đổi Long → Integer

         int insert(ProductVariant variant);

         int update(ProductVariant variant);

         int deleteById(@Param("variantId") Integer variantId);                         // Đổi Long → Integer

         List<ProductVariant> findAll();

         int updateStock(@Param("variantId") Integer variantId, @Param("quantity") Integer quantity);  // Đổi Long → Integer

         ProductVariant findBySkuCode(@Param("skuCode") String skuCode);                // Đổi từ findByCode

         // XÓA: findActiveByProductId (vì DB không có is_active)
     }
