 package com.fpl.edu.shoeStore.product.mapper;

     import java.util.List;

     import org.apache.ibatis.annotations.Mapper;
     import org.apache.ibatis.annotations.Param;

     import com.fpl.edu.shoeStore.product.entity.ProductVariant;

     @Mapper
     public interface ProductVariantMapper {

         List<ProductVariant> findByProductId(@Param("productId") Long productId);

         ProductVariant findById(@Param("variantId") Long variantId);

         int insert(ProductVariant variant);

         int update(ProductVariant variant);

         int deleteById(@Param("variantId") Long variantId);

         List<ProductVariant> findAll();

         List<ProductVariant> findActiveByProductId(@Param("productId") Long productId);

         int updateStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);

         ProductVariant findByCode(@Param("code") String code);
     }