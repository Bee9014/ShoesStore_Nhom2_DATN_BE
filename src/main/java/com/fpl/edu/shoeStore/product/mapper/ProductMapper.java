package com.fpl.edu.shoeStore.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.product.entity.Product;

@Mapper
public interface ProductMapper {

    // ==================== CƠ BẢN (CRUD) ====================

    /**
     * Lấy toàn bộ danh sách sản phẩm (không phân trang).
     */
    List<Product> findAll();

    /**
     * Lấy thông tin chi tiết của một sản phẩm dựa trên ID.
     */
    Product findById(@Param("productId") Integer productId);

    /**
     * Tìm kiếm sản phẩm dựa trên tiêu đề (title) chính xác.
     * Thường dùng để kiểm tra trùng lặp tên sản phẩm.
     */
    Product findByTitle(@Param("title") String title);

    /**
     * Thêm mới một sản phẩm vào hệ thống.
     * ID tự tăng từ SQL Server sẽ được MyBatis gán ngược lại vào đối tượng product.
     */
    int insert(Product product);

    /**
     * Cập nhật thông tin sản phẩm (giá, mô tả, trạng thái, v.v.).
     */
    int update(Product product);

    /**
     * Xóa vĩnh viễn một sản phẩm khỏi cơ sở dữ liệu dựa trên ID.
     */
    int deleteById(@Param("productId") Integer productId);

    // ==================== PHÂN TRANG & BỘ LỌC (ADMIN/SHOP) ====================

    /**
     * Truy vấn danh sách sản phẩm với nhiều bộ lọc (Danh mục, Tên, Trạng thái) và phân trang.
     * Lưu ý: Trong XML phải dùng ORDER BY kết hợp OFFSET ... ROWS FETCH NEXT ... ROWS ONLY.
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
     * Đếm tổng số lượng sản phẩm thỏa mãn các điều kiện lọc để tính toán phân trang.
     */
    long countAll(
        @Param("categoryId") Integer categoryId,
        @Param("title") String title,
        @Param("status") String status,
        @Param("isActive") Boolean isActive
    );

    // ==================== THỐNG KÊ & HIỂN THỊ (MARKETING) ====================

    /**
     * Tăng số lượt xem (view count) của sản phẩm thêm 1 đơn vị.
     * Dùng để theo dõi mức độ quan tâm của khách hàng.
     */
    void incrementViewCount(Integer productId);

    /**
     * Lấy danh sách các sản phẩm nổi bật (Featured) được đánh dấu để hiển thị ở trang chủ.
     */
    List<Product> findTopFeatured();

    /**
     * Lấy danh sách sản phẩm bán chạy nhất dựa trên số lượng đơn hàng đã hoàn tất.
     * Trong XML SQL Server: Sử dụng SELECT TOP (10) ...
     */
    List<Product> findBestSellers();

    /**
     * Đếm tổng số lượng sản phẩm đang có trong database cho Dashboard báo cáo.
     */
    Long countAllProducts();
    
    /**
     * Tìm kiếm và phân trang cho danh sách 50 sản phẩm bán chạy nhất.
     */
    List<Product> findBestFiftySellers(
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    /**
     * Đếm tổng số sản phẩm bán chạy để phục vụ phân trang cho mục "Best Sellers".
     */
    Long countBestSellers();
    
    // ==================== TÌM KIẾM NÂNG CAO ====================

    /**
     * Tìm kiếm sản phẩm theo từ khóa (keyword) dựa trên tên hoặc thương hiệu.
     * Lưu ý XML SQL Server: Dùng cú pháp LIKE '%' + #{keyword} + '%' để tìm kiếm tương đối.
     */
    List<Product> findProductsBySearch(
        @Param("keyword") String keyword,
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    /**
     * Đếm tổng số kết quả tìm kiếm được để hiển thị thông báo "Tìm thấy X sản phẩm".
     */
    Long countSearchResults(@Param("keyword") String keyword);
}