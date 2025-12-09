package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

@Mapper
public interface ProductMapper {
    List<Product> findAll();
    Product findById(@Param("productId") Long productId);
    Product findByName(@Param("name") String name);
    int insert(Product product);
    int update(Product product);
    int deleteById(@Param("productId") Long productId);

    // Tìm kiếm & phân trang tương tự như user
    List<Product> findAllPaged(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("slug") String slug,
            @Param("isActive") Boolean isActive,
            @Param("offset") int offset,
            @Param("size") int size
    );
    long countAll(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("slug") String slug,
            @Param("isActive") Boolean isActive
    );
}
