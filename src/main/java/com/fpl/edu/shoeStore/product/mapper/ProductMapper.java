package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

@Mapper
public interface ProductMapper {

    /** Lấy danh sách toàn bộ sản phẩm không lọc. */
    List<Product> findAll();

    Product findByTitle(@Param("title") String title);

    /** Tìm chi tiết sản phẩm theo mã ID. */
    Product findById(@Param("productId") int productId);

    /** Thêm sản phẩm mới. ID tự tăng sẽ gán vào đối tượng product. */
    int insert(Product product);

    /** Cập nhật thông tin chi tiết sản phẩm. */
    int update(Product product);

    int deleteById(@Param("productId") Integer id);

    /**
     * * Phân trang và lọc sản phẩm.
     * XML dùng parameterType="map", nên cần @Param chính xác.
     */
    List<Product> findAllPaged(
            @Param("categoryId") Integer categoryId,
            @Param("title") String title,
            @Param("status") String status,
            @Param("isActive") Boolean isActive,
            @Param("offset") int offset,
            @Param("size") int size);

    /** Đếm tổng số lượng sản phẩm theo bộ lọc (title, categoryId, status). */
    long countAll(
            @Param("categoryId") Integer categoryId,
            @Param("title") String title,
            @Param("status") String status,
            @Param("isActive") Boolean isActive);

    void incrementViewCount(Integer productId);

    List<Product> findTopFeatured();

    List<Product> findBestSellers();

    Long countAllProducts();

    List<Product> findBestFiftySellers(
            @Param("size") Integer size,
            @Param("offset") Integer offset);

    Long countBestSellers();

    List<Product> findProductsBySearch(
            @Param("keyword") String keyword,
            @Param("size") Integer size,
            @Param("offset") Integer offset);

    Long countSearchResults(@Param("keyword") String keyword);

}