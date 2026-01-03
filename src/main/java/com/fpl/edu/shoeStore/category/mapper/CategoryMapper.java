package com.fpl.edu.shoeStore.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.entity.Category;

@Mapper
public interface CategoryMapper {
    
    // ==================== BASIC CRUD (THAO TÁC CƠ BẢN) ====================
    
    /**
     * Lấy danh sách tất cả các danh mục có trong hệ thống.
     */
    List<Category> findAll();
    
    /**
     * Tìm kiếm một danh mục cụ thể thông qua mã ID.
     */
    Category findById(@Param("categoryId") Integer categoryId);
    
    /**
     * Tìm kiếm danh mục dựa trên tên chính xác (thường dùng để kiểm tra trước khi tạo mới).
     */
    Category findByName(@Param("name") String name);
    
    /**
     * Thêm mới một danh mục vào cơ sở dữ liệu.
     * Trả về số dòng bị tác động (1 nếu thành công).
     */
    int insert(Category category);
    
    /**
     * Cập nhật thông tin của một danh mục hiện có (tên, mô tả, v.v.).
     */
    int update(Category category);
    
    /**
     * Xóa tạm thời danh mục bằng cách cập nhật trạng thái hoạt động (is_active = false).
     * Giúp giữ lại dữ liệu lịch sử thay vì xóa vĩnh viễn.
     */
    int softDelete(@Param("categoryId") Integer categoryId);
    
    /**
     * Xóa vĩnh viễn một danh mục khỏi cơ sở dữ liệu dựa trên ID.
     */
    int deleteById(@Param("categoryId") Integer categoryId);
    
    // ==================== PAGING & FILTERING (PHÂN TRANG & BỘ LỌC) ====================
    
    /**
     * Lấy danh sách danh mục có hỗ trợ tìm kiếm theo tên, lọc trạng thái và phân trang.
     * Phù hợp cho giao diện quản lý danh mục ở trang Admin.
     */
    List<CategoryDtoResponse> findAllPaged(
        @Param("search") String search,
        @Param("isActive") Boolean isActive,
        @Param("offset") int offset,
        @Param("size") int size
    );
    
    /**
     * Đếm tổng số danh mục thỏa mãn điều kiện lọc để phục vụ việc tính toán số trang.
     */
    long countAll(
        @Param("search") String search,
        @Param("isActive") Boolean isActive
    );
    
    // ==================== SELECT OPTIONS (LỰA CHỌN) ====================
    
    /**
     * Lấy danh sách các danh mục đang hoạt động để hiển thị lên dropdown hoặc menu chọn lọc.
     */
    List<Category> findAllActive();
    
    // ==================== VALIDATION QUERIES (KIỂM TRA RÀNG BUỘC) ====================
    
    /**
     * Đếm số lượng sản phẩm thuộc về danh mục này. 
     * Dùng để kiểm tra trước khi xóa (không cho xóa nếu còn sản phẩm).
     */
    int countProductsByCategory(@Param("categoryId") Integer categoryId);
    
    /**
     * Đếm số lượng danh mục con thuộc về danh mục này.
     */
    int countChildCategories(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra nhanh xem một mã ID danh mục có tồn tại trong hệ thống hay không.
     */
    boolean existsById(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra xem tên danh mục đã tồn tại chưa. 
     * Tham số excludeId dùng để bỏ qua ID hiện tại khi thực hiện cập nhật (update).
     */
    boolean existsByName(
        @Param("name") String name,
        @Param("excludeId") Integer excludeId
    );
}