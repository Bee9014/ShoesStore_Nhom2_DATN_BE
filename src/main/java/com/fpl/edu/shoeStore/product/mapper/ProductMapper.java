package com.fpl.edu.shoeStore.product.mapper;

import com.fpl.edu.shoeStore.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> findAll();

    Product findById(@Param("productId") Integer productId);

    Product findByTitle(@Param("title") String title);

    int insert(Product product);

    int update(Product product);

    int deleteById(@Param("productId") Integer productId);

    List<Product> findAllPaged(
            @Param("categoryId") Integer categoryId,
            @Param("title") String title,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(
            @Param("categoryId") Integer categoryId,
            @Param("title") String title,
            @Param("status") String status
    );
}