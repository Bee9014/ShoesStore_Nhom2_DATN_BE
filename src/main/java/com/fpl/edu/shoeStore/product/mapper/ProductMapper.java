package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

@Mapper
public interface ProductMapper {

    /**
     * Lấy danh sách toàn bộ sản phẩm.
     */
    List<Product> findAll();

    /**
     * Tìm chi tiết sản phẩm theo ID.
     */
    Product findById(@Param("productId") Integer productId);

    /**
     * Tìm sản phẩm theo tiêu đề chính xác để kiểm tra trùng lặp.
     */
    Product findByTitle(@Param("title") String title);

    /**
     * Thêm mới sản phẩm. ID tự tăng sẽ được gán lại vào object 'product'.
     */
    int insert(Product product);

    /**
     * Cập nhật thông tin sản phẩm (mô tả, giá, trạng thái...).
     */
    int update(Product product);

    /**
     * Xóa sản phẩm khỏi hệ thống theo ID.
     */
    int deleteById(@Param("productId") Integer productId);

    /**
     * Tìm kiếm và phân trang sản phẩm theo nhiều tiêu chí (Danh mục, Tên, Trạng thái).
     */
    List<Product> findAllPaged(
        @Param("categoryId") Integer categoryId,
        @Param("title") String title,
        @Param("status") String status,
        @Param("isActive") Boolean isActive,
        @Param("offset") int offset,
        @Param("size") int size
    );

    /**
     * Đếm tổng số sản phẩm theo bộ lọc để phục vụ phân trang Admin/Shop.
     */
    long countAll(
        @Param("categoryId") Integer categoryId,
        @Param("title") String title,
        @Param("status") String status,
        @Param("isActive") Boolean isActive
    );

    /**
     * Tăng số lượt xem sản phẩm lên 1 (Dùng cho thống kê độ hot).
     */
    void incrementViewCount(@Param("productId") Integer productId);

    /**
     * Lấy danh sách sản phẩm nổi bật được đánh dấu hiển thị trang chủ.
     */
    List<Product> findTopFeatured();

    /**
     * Lấy danh sách sản phẩm bán chạy nhất dựa trên lịch sử đơn hàng.
     */
    List<Product> findBestSellers();

    /**
     * Tìm kiếm sản phẩm theo từ khóa (Keyword) trong tên hoặc thương hiệu.
     */
    List<Product> findProductsBySearch(
        @Param("keyword") String keyword,
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
}