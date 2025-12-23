package com.fpl.edu.shoeStore.category.mapper;

import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    // ==================== BASIC CRUD ====================
    
    /**
     * Lấy tất cả categories
     */
    List<Category> findAll();
    
    /**
     * Lấy category theo ID
     */
    Category findById(@Param("categoryId") Integer categoryId);
    
    /**
     * Lấy category theo name (check duplicate)
     */
    Category findByName(@Param("name") String name);
    
    /**
     * Thêm category mới
     */
    int insert(Category category);
    
    /**
     * Cập nhật category
     */
    int update(Category category);
    
    /**
     * Soft delete (set is_active = 0)
     */
    int softDelete(@Param("categoryId") Integer categoryId);
    
    /**
     * Hard delete (for testing only)
     */
    int deleteById(@Param("categoryId") Integer categoryId);
    
    // ==================== PAGING & FILTERING ====================
    
    /**
     * Lấy categories có phân trang + filter
     * @param search - Tìm theo tên (LIKE)
     * @param isActive - Filter theo trạng thái
     * @param offset - Vị trí bắt đầu
     * @param size - Số lượng records
     * @return List categories với parentName được JOIN
     */
    List<CategoryDtoResponse> findAllPaged(
        @Param("search") String search,
        @Param("isActive") Boolean isActive,
        @Param("offset") int offset,
        @Param("size") int size
    );
    
    /**
     * Đếm tổng số categories (for pagination)
     */
    long countAll(
        @Param("search") String search,
        @Param("isActive") Boolean isActive
    );
    
    // ==================== SELECT OPTIONS ====================
    
    /**
     * Lấy tất cả active categories (for dropdown)
     * Sorted by name A-Z
     */
    List<Category> findAllActive();
    
    // ==================== VALIDATION QUERIES ====================
    
    /**
     * Kiểm tra category có products không
     * @return số lượng products thuộc category
     */
    int countProductsByCategory(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra category có subcategories không
     * @return số lượng child categories
     */
    int countChildCategories(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra tồn tại theo ID
     */
    boolean existsById(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra tên trùng (excluding current ID for update)
     */
    boolean existsByName(
        @Param("name") String name,
        @Param("excludeId") Integer excludeId
    );
}
