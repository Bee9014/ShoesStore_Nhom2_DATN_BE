package com.fpl.edu.shoeStore.category.controller;

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Category REST Controller
 * Base URL: /api/v1/categories
 * 
 * Note: This is a simplified version using mock data.
 * TODO: Implement full Category entity, service, mapper when needed.
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    /**
     * GET /api/v1/categories - Get all categories
     * 
     * @return ApiResponse with list of categories
     */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAllCategories() {
        try {
            // TODO: Replace with actual database query
            // For now, return mock data based on database schema
            List<Map<String, Object>> categories = Arrays.asList(
                    Map.of("categoryId", 1, "name", "Giày thể thao"),
                    Map.of("categoryId", 2, "name", "Giày công sở"),
                    Map.of("categoryId", 3, "name", "Giày cao gót"),
                    Map.of("categoryId", 4, "name", "Giày sandal"),
                    Map.of("categoryId", 5, "name", "Giày lười")
            );
            
            return ApiResponse.<List<Map<String, Object>>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách danh mục thành công")
                    .data(categories)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<Map<String, Object>>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy danh sách danh mục: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
