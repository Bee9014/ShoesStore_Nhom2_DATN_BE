package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

@Mapper
public interface ProductMapper {
    List<Product> findAll();

    Product findById(@Param("productId") Integer productId);

    Product findByTitle(@Param("title") String title);

    int insert(Product product);

    int update(Product product);

    int deleteById(@Param("productId") Integer productId);

    // SQL Server: Dùng OFFSET/FETCH. Phải có ORDER BY trong XML.
    List<Product> findAllPaged(
        @Param("categoryId") Integer categoryId,
        @Param("title") String title,
        @Param("status") String status,
        @Param("isActive") Boolean isActive,
        @Param("offset") int offset,
        @Param("size") int size
    );

    long countAll(
        @Param("categoryId") Integer categoryId,
        @Param("title") String title,
        @Param("status") String status,
        @Param("isActive") Boolean isActive
    );

    void incrementViewCount(Integer productId);

    List<Product> findTopFeatured();

    // SQL Server XML: SELECT TOP (10) ...
    List<Product> findBestSellers();

    Long countAllProducts();
    
    // Tìm 50 sản phẩm bán chạy có phân trang
    List<Product> findBestFiftySellers(
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    Long countBestSellers();
    
    /**
     * Tìm kiếm sản phẩm. 
     * Lưu ý XML SQL Server: Dùng LIKE '%' + #{keyword} + '%'
     */
    List<Product> findProductsBySearch(
        @Param("keyword") String keyword,
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    Long countSearchResults(@Param("keyword") String keyword);
}