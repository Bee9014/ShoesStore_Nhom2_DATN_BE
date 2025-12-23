package com.fpl.edu.shoeStore.category.service.impl;

import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.category.converter.CategoryConverter;
import com.fpl.edu.shoeStore.category.dto.request.CategoryDtoRequest;
import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.entity.Category;
import com.fpl.edu.shoeStore.category.mapper.CategoryMapper;
import com.fpl.edu.shoeStore.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryDtoResponse> findAllPaged(
            String search,
            Boolean isActive,
            int page,
            int size
    ) {
        // Validate page & size
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100; // Max limit

        int offset = (page - 1) * size;

        // Query database
        List<CategoryDtoResponse> content = categoryMapper.findAllPaged(
            search, isActive, offset, size
        );

        long totalElements = categoryMapper.countAll(search, isActive);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<CategoryDtoResponse>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDtoResponse> findAllActive() {
        List<Category> categories = categoryMapper.findAllActive();
        return CategoryConverter.toResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDtoResponse findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Category ID không hợp lệ");
        }

        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }

        return CategoryConverter.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryDtoResponse createCategory(CategoryDtoRequest request) {
        // Validation 1: Check duplicate name
        if (categoryMapper.existsByName(request.getName(), null)) {
            throw new RuntimeException("Tên danh mục đã tồn tại: " + request.getName());
        }

        // Validation 2: If has parent_id, check parent exists
        if (request.getParentId() != null) {
            if (!categoryMapper.existsById(request.getParentId())) {
                throw new RuntimeException("Danh mục cha không tồn tại với ID: " + request.getParentId());
            }
        }

        // Convert & set defaults
        Category category = CategoryConverter.toEntity(request);
        
        // Insert to database
        int result = categoryMapper.insert(category);
        if (result <= 0) {
            throw new RuntimeException("Tạo danh mục thất bại");
        }

        log.info("Created category: {} with ID: {}", category.getName(), category.getCategoryId());
        return CategoryConverter.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryDtoResponse updateCategory(Integer id, CategoryDtoRequest request) {
        // Validation 1: Check exists
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }

        // Validation 2: Check duplicate name (exclude current ID)
        if (categoryMapper.existsByName(request.getName(), id)) {
            throw new RuntimeException("Tên danh mục đã tồn tại: " + request.getName());
        }

        // Validation 3: Check parent_id (không thể set parent = chính mình)
        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new RuntimeException("Danh mục không thể là cha của chính nó");
        }

        // Validation 4: If has parent_id, check parent exists
        if (request.getParentId() != null) {
            if (!categoryMapper.existsById(request.getParentId())) {
                throw new RuntimeException("Danh mục cha không tồn tại với ID: " + request.getParentId());
            }
        }

        // Update fields
        if (request.getParentId() != null) existing.setParentId(request.getParentId());
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getUrl() != null) existing.setUrl(request.getUrl());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());
        if (request.getSortOrder() != null) existing.setSortOrder(request.getSortOrder());
        if (request.getUpdatedBy() != null) existing.setUpdatedBy(request.getUpdatedBy());
        
        existing.setUpdatedAt(LocalDateTime.now());

        // Update database
        int result = categoryMapper.update(existing);
        if (result <= 0) {
            throw new RuntimeException("Cập nhật danh mục thất bại");
        }

        log.info("Updated category ID: {}", id);
        return CategoryConverter.toResponse(existing);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        // Validation 1: Check exists
        if (!categoryMapper.existsById(id)) {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }

        // Validation 2: Check has products
        int productCount = categoryMapper.countProductsByCategory(id);
        if (productCount > 0) {
            throw new RuntimeException(
                "Không thể xóa danh mục đã chứa " + productCount + " sản phẩm. " +
                "Vui lòng di chuyển hoặc xóa sản phẩm trước."
            );
        }

        // Validation 3: Check has child categories
        int childCount = categoryMapper.countChildCategories(id);
        if (childCount > 0) {
            throw new RuntimeException(
                "Không thể xóa danh mục đang là cha của " + childCount + " danh mục con. " +
                "Vui lòng xóa hoặc di chuyển danh mục con trước."
            );
        }

        // Soft delete
        int result = categoryMapper.softDelete(id);
        if (result <= 0) {
            throw new RuntimeException("Xóa danh mục thất bại");
        }

        log.info("Soft deleted category ID: {}", id);
    }
}
