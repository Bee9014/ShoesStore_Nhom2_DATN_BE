package com.fpl.edu.shoeStore.category.service;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.category.dto.request.CategoryDtoRequest;
import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;

import java.util.List;

public interface CategoryService {
    
    /**
     * Lấy danh sách có phân trang (Admin Table)
     */
    PageResponse<CategoryDtoResponse> findAllPaged(
        String search,
        Boolean isActive,
        int page,
        int size
    );
    
    /**
     * Lấy tất cả active categories (Dropdown)
     */
    List<CategoryDtoResponse> findAllActive();
    
    /**
     * Lấy category theo ID
     */
    CategoryDtoResponse findById(Integer id);
    
    /**
     * Tạo category mới
     * Validation: Check duplicate name
     */
    CategoryDtoResponse createCategory(CategoryDtoRequest request);
    
    /**
     * Cập nhật category
     * Validation: Check duplicate name (exclude current ID)
     */
    CategoryDtoResponse updateCategory(Integer id, CategoryDtoRequest request);
    
    /**
     * Xóa category (Soft delete)
     * Validation:
     * - Check has products -> Throw exception
     * - Check has child categories -> Throw exception
     */
    void deleteCategory(Integer id);
}
