package com.fpl.edu.shoeStore.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.entity.Category;

@Mapper
public interface CategoryMapper {
    
    /**
     * Lấy toàn bộ danh mục sản phẩm sắp xếp theo sort_order và tên.
     */
    List<Category> findAll();
    
    /**
     * Tìm danh mục theo ID.
     */
    Category findById(@Param("categoryId") Integer categoryId);

    /**
     * Tìm danh mục theo tên chính xác (Lấy bản ghi đầu tiên).
     */
    Category findByName(@Param("name") String name);
    
    /**
     * Thêm danh mục mới. ID tự sinh sẽ được gán vào object.
     */
    int insert(Category category);
    
    /**
     * Cập nhật toàn bộ thông tin danh mục bao gồm cả người cập nhật.
     */
    int update(Category category);
    
    /**
     * Ẩn danh mục (isActive = 0).
     */
    int softDelete(@Param("categoryId") Integer categoryId);

    /**
     * Xóa vĩnh viễn danh mục khỏi cơ sở dữ liệu.
     */
    int deleteById(@Param("categoryId") Integer categoryId);
    
    /**
     * Tìm kiếm và phân trang danh mục trả về DTO bao gồm số lượng sản phẩm.
     * @param search Từ khóa tìm kiếm tên danh mục.
     * @param isActive Trạng thái hoạt động.
     * @param offset Vị trí bắt đầu.
     * @param size Số lượng bản ghi mỗi trang.
     */
    List<CategoryDtoResponse> findAllPaged(
        @Param("search") String search, 
        @Param("isActive") Boolean isActive, 
        @Param("offset") int offset, 
        @Param("size") int size
    );

    /**
     * Đếm tổng số danh mục theo bộ lọc để tính toán phân trang.
     */
    long countAll(@Param("search") String search, @Param("isActive") Boolean isActive);

    /**
     * Kiểm tra danh mục có tồn tại hay không qua ID.
     */
    boolean existsById(@Param("categoryId") Integer categoryId);

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa (có hỗ trợ loại trừ ID hiện tại).
     */
    boolean existsByName(@Param("name") String name, @Param("excludeId") Integer excludeId);

    /**
     * Lấy danh sách các danh mục đang hoạt động.
     */
    List<Category> findAllActive();

    /**
     * Đếm số lượng sản phẩm đang thuộc về danh mục này.
     */
    int countProductsByCategory(@Param("categoryId") Integer categoryId);
}