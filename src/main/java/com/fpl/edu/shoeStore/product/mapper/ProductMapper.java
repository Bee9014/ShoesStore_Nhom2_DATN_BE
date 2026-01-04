package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

@Mapper
public interface ProductMapper {

    /** Lấy danh sách toàn bộ sản phẩm không lọc. */
    List<Product> findAll();

    /** Tìm chi tiết sản phẩm theo mã ID. */
    Product findById(@Param("productId") int productId);

    /** Thêm sản phẩm mới. ID tự tăng sẽ gán vào đối tượng product. */
    int insert(Product product);

    /** Cập nhật thông tin chi tiết sản phẩm. */
    int update(Product product);

    /** * Phân trang và lọc sản phẩm. 
     * XML dùng parameterType="map", nên cần @Param chính xác.
     */
    List<Product> findAllPaged(
        @Param("title") String title,
        @Param("categoryId") Integer categoryId,
        @Param("offset") int offset, 
        @Param("size") int size
    );

    /** Đếm tổng số lượng sản phẩm theo bộ lọc (title, categoryId, status). */
    long countAll(
        @Param("categoryId") Integer categoryId,
        @Param("title") String title,
        @Param("status") String status
    );
}