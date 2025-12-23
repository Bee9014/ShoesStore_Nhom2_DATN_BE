package com.fpl.edu.shoeStore.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // 👈 Import quan trọng
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // 👈 Import quan trọng
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    // --- CÁC HÀM GET GIỮ NGUYÊN ---

    @GetMapping
    public ApiResponse<PageResponse<ProductDtoResponse>> getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponse<ProductDtoResponse> pageResponse = productService.findAllPaged(
                    categoryId, title, status, isActive, page, size
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

    @GetMapping("/{id}")
    public ApiResponse<ProductDtoResponse> getProductById(@PathVariable Integer id) {
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
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @GetMapping("/search/title")
    public ApiResponse<ProductDtoResponse> getProductByTitle(@RequestParam String title) {
        try {
            ProductDtoResponse product = productService.findByTitle(title);
            if (product == null) {
                return ApiResponse.<ProductDtoResponse>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy sản phẩm với tiêu đề: " + title)
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
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // --- THAY ĐỔI Ở ĐÂY: HỖ TRỢ UPLOAD ẢNH (CREATE) ---

    /**
     * POST /api/v1/products
     * Sử dụng FormData để gửi thông tin và file ảnh.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 👈 Chỉ định nhận Multipart
    public ApiResponse<ProductDtoResponse> createProduct(
            @ModelAttribute ProductDtoRequest request, // 👈 Đổi @RequestBody -> @ModelAttribute để nhận FormData
            @RequestParam(value = "file", required = false) MultipartFile file // 👈 Nhận file ảnh
    ) {
        try {
            // Gọi Service với tham số file
            ProductDtoResponse created = productService.createProduct(request, file);

            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Tạo sản phẩm thành công")
                    .data(created)
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để debug
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi tạo sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // --- THAY ĐỔI Ở ĐÂY: HỖ TRỢ UPLOAD ẢNH (UPDATE) ---

    /**
     * PUT /api/v1/products/{id}
     * Cập nhật thông tin và (tùy chọn) cập nhật ảnh mới.
     */
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 👈 Chỉ định nhận Multipart
    public ApiResponse<ProductDtoResponse> updateProduct(
            @PathVariable Integer id,
            @ModelAttribute ProductDtoRequest request, // 👈 Đổi @RequestBody -> @ModelAttribute
            @RequestParam(value = "file", required = false) MultipartFile file // 👈 Nhận file ảnh mới (nếu có)
    ) {
        try {
            ProductDtoResponse updated = productService.updateProduct(id, request, file);

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
            e.printStackTrace();
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi cập nhật sản phẩm: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // --- DELETE GIỮ NGUYÊN ---

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Integer id) {
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