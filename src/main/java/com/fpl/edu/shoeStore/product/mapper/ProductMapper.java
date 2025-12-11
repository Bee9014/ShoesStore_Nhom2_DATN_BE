package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

/**
 * Product Mapper Interface
 * MyBatis mapper for products table
 */
@Mapper
public interface ProductMapper {
    /**
     * Find all active products
     */
    List<Product> findAll();
    
    /**
     * Find product by ID
     */
    Product findById(@Param("productId") Long productId);
    
    /**
     * Find product by name
     */
    Product findByName(@Param("name") String name);
    
    /**
     * Insert new product
     */
    int insert(Product product);
    
    /**
     * Update product
     */
    int update(Product product);
    
    /**
     * Soft delete product (set is_active = 0)
     */
    int deleteById(@Param("productId") Long productId);

    /**
     * Find products with pagination and filters
     */
    List<Product> findAllPaged(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("url") String url,                    // Updated: slug → url
            @Param("isActive") Boolean isActive,
            @Param("offset") int offset,
            @Param("size") int size
    );
    
    /**
     * Count products with filters (for pagination)
     */
    long countAll(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("url") String url,                    // Updated: slug → url
            @Param("isActive") Boolean isActive
    );
}
