package com.fpl.edu.shoeStore.category.controller;

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.category.dto.request.CategoryDtoRequest;
import com.fpl.edu.shoeStore.category.dto.response.CategoryDtoResponse;
import com.fpl.edu.shoeStore.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category REST Controller
 * Base URL: /api/v1/categories
 * 
 * Endpoints:
 * 1. GET /api/v1/categories - Paged list (Admin Table)
 * 2. GET /api/v1/categories/select - All active (Dropdown)
 * 3. GET /api/v1/categories/{id} - Get by ID
 * 4. POST /api/v1/categories - Create
 * 5. PUT /api/v1/categories/{id} - Update
 * 6. DELETE /api/v1/categories/{id} - Soft delete
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 1. GET /api/v1/categories - Lấy danh sách phân trang (Admin Table)
     */
    @GetMapping
    public ApiResponse<PageResponse<CategoryDtoResponse>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive
    ) {
        try {
            PageResponse<CategoryDtoResponse> pageResponse = categoryService.findAllPaged(
                search, isActive, page, size
            );

            return ApiResponse.<PageResponse<CategoryDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách danh mục thành công")
                    .data(pageResponse)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PageResponse<CategoryDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy danh sách danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 2. GET /api/v1/categories/select - Lấy tất cả active categories (Dropdown)
     */
    @GetMapping("/select")
    public ApiResponse<List<CategoryDtoResponse>> getAllActiveCategories() {
        try {
            List<CategoryDtoResponse> categories = categoryService.findAllActive();

            return ApiResponse.<List<CategoryDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách danh mục hoạt động thành công")
                    .data(categories)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<CategoryDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy danh sách danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 3. GET /api/v1/categories/{id} - Get category by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryDtoResponse> getCategoryById(@PathVariable Integer id) {
        try {
            CategoryDtoResponse category = categoryService.findById(id);

            if (category == null) {
                return ApiResponse.<CategoryDtoResponse>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy danh mục với ID: " + id)
                        .data(null)
                        .build();
            }

            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin danh mục thành công")
                    .data(category)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy thông tin danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 4. POST /api/v1/categories - Create new category
     */
    @PostMapping
    public ApiResponse<CategoryDtoResponse> createCategory(
            @RequestBody @Valid CategoryDtoRequest request
    ) {
        try {
            CategoryDtoResponse created = categoryService.createCategory(request);

            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Tạo danh mục thành công")
                    .data(created)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tạo danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 5. PUT /api/v1/categories/{id} - Update category
     */
    @PutMapping("/{id}")
    public ApiResponse<CategoryDtoResponse> updateCategory(
            @PathVariable Integer id,
            @RequestBody @Valid CategoryDtoRequest request
    ) {
        try {
            CategoryDtoResponse updated = categoryService.updateCategory(id, request);

            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật danh mục thành công")
                    .data(updated)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<CategoryDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi cập nhật danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 6. DELETE /api/v1/categories/{id} - Soft delete category
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Xóa danh mục thành công")
                    .data(null)
                    .build();
        } catch (RuntimeException e) {
            // Business logic errors (has products, has children)
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi xóa danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
