package com.fpl.edu.shoeStore.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.entity.Category;

@Mapper
public interface CategoryMapper {
    
    // ==================== BASIC CRUD ====================
    
    List<Category> findAll();
    
    Category findById(@Param("categoryId") Integer categoryId);
    
    Category findByName(@Param("name") String name);
    
    // SQL Server sẽ trả về số dòng bị tác động (1 nếu thành công)
    int insert(Category category);
    
    int update(Category category);
    
    // Soft delete thực chất là lệnh UPDATE
    int softDelete(@Param("categoryId") Integer categoryId);
    
    int deleteById(@Param("categoryId") Integer categoryId);
    
    // ==================== PAGING & FILTERING ====================
    
    /**
     * Lưu ý: Trong XML tương ứng, offset và size sẽ được dùng cho 
     * OFFSET ... ROWS FETCH NEXT ... ROWS ONLY
     */
    List<CategoryDtoResponse> findAllPaged(
        @Param("search") String search,
        @Param("isActive") Boolean isActive,
        @Param("offset") int offset,
        @Param("size") int size
    );
    
    // SQL Server COUNT trả về kiểu Long (BIGINT)
    long countAll(
        @Param("search") String search,
        @Param("isActive") Boolean isActive
    );
    
    // ==================== SELECT OPTIONS ====================
    
    List<Category> findAllActive();
    
    // ==================== VALIDATION QUERIES ====================
    
    // Trả về số lượng sản phẩm liên quan
    int countProductsByCategory(@Param("categoryId") Integer categoryId);
    
    // Trả về số lượng danh mục con
    int countChildCategories(@Param("categoryId") Integer categoryId);
    
    /**
     * MyBatis tự động convert kết quả COUNT(*) > 0 thành true cho SQL Server
     */
    boolean existsById(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra tên trùng, dùng cho cả insert (excludeId = null) 
     * và update (excludeId = currentId)
     */
    boolean existsByName(
        @Param("name") String name,
        @Param("excludeId") Integer excludeId
    );
}