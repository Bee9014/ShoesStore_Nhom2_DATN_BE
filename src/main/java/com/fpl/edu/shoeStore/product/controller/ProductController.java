package com.fpl.edu.shoeStore.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.common.handler.PageResponse;
import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
import com.fpl.edu.shoeStore.product.service.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * Product REST Controller
 * Base URL: /api/v1/products
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/v1/products - Get all products with pagination and filters
     * 
     * @param categoryId Filter by category ID (optional)
     * @param name       Filter by product name (optional, partial match)
     * @param url        Filter by URL (optional, partial match)
     * @param isActive   Filter by active status (optional)
     * @param page       Page number (default: 1)
     * @param size       Page size (default: 10)
     * @return ApiResponse with PageResponse of products
     */
    @GetMapping
    public ApiResponse<PageResponse<ProductDtoResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String url,        // Updated: slug → url
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponse<ProductDtoResponse> pageResponse = productService.findAllPaged(
                    categoryId, name, url, isActive, page, size
            );
            
            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách sản phẩm thành công")
                    .data(pageResponse)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/v1/products/{id} - Get product by ID
     * 
     * @param id Product ID
     * @return ApiResponse with product data
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDtoResponse> getProductById(@PathVariable Long id) {
        try {
            ProductDtoResponse product = productService.findById(id);
            
            if (product == null) {
                return ApiResponse.<ProductDtoResponse>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy sản phẩm với ID: " + id)
                        .data(null)
                        .build();
            }
            
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin sản phẩm thành công")
                    .data(product)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi lấy thông tin sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/v1/products/search/name - Search product by exact name
     * 
     * @param name Product name
     * @return ApiResponse with product data
     */
    @GetMapping("/search/name")
    public ApiResponse<ProductDtoResponse> getProductByName(@RequestParam String name) {
        try {
            ProductDtoResponse product = productService.findByName(name);
            
            if (product == null) {
                return ApiResponse.<ProductDtoResponse>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy sản phẩm với tên: " + name)
                        .data(null)
                        .build();
            }
            
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Tìm kiếm sản phẩm thành công")
                    .data(product)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * POST /api/v1/products - Create new product
     * 
     * @param request Product data
     * @return ApiResponse with created product
     */
    @PostMapping
    public ApiResponse<ProductDtoResponse> createProduct(@RequestBody ProductDtoRequest request) {
        try {
            ProductDtoResponse created = productService.createProduct(request);
            
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Tạo sản phẩm thành công")
                    .data(created)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi tạo sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * PUT /api/v1/products/{id} - Update product
     * 
     * @param id      Product ID
     * @param request Updated product data
     * @return ApiResponse with updated product
     */
    @PutMapping("/{id}")
    public ApiResponse<ProductDtoResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDtoRequest request
    ) {
        try {
            ProductDtoResponse updated = productService.updateProduct(id, request);
            
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật sản phẩm thành công")
                    .data(updated)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi cập nhật sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * DELETE /api/v1/products/{id} - Delete product (soft delete)
     * 
     * @param id Product ID
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            
            return ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Xóa sản phẩm thành công")
                    .data(null)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi khi xóa sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}