package com.fpl.edu.shoeStore.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.category.entity.Category;

@Mapper
public interface CategoryMapper {
    
    /**
     * Lấy toàn bộ danh mục sản phẩm để hiển thị menu.
     */
    List<Category> findAll();
    
    /**
     * Tìm danh mục theo ID.
     */
    Category findById(@Param("categoryId") Integer categoryId);
    
    /**
     * Thêm danh mục mới.
     */
    int insert(Category category);
    
    /**
     * Cập nhật tên hoặc trạng thái danh mục.
     */
    int update(Category category);
    
    /**
     * Ẩn danh mục (Soft Delete) thay vì xóa thật trong DB.
     */
    int softDelete(@Param("categoryId") Integer categoryId);
    
    /**
     * Kiểm tra xem tên danh mục đã tồn tại chưa để tránh trùng.
     */
    boolean existsByName(@Param("name") String name, @Param("excludeId") Integer excludeId);
}