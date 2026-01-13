package com.fpl.edu.shoeStore.product.controller;

import java.util.List; // 👈 Nhớ import List

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/featured")
    public ApiResponse<List<ProductDtoResponse>> getFeaturedProducts() {
        try {
            List<ProductDtoResponse> featured = productService.getFeaturedProducts();
            return ApiResponse.<List<ProductDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách sản phẩm nổi bật thành công")
                    .data(featured)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductDtoResponse>> getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageResponse<ProductDtoResponse> pageResponse = productService.findAllPaged(
                    categoryId, title, status, isActive, page, size);

            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(true).statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách sản phẩm thành công").data(pageResponse).build();
        } catch (Exception e) {
            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(false).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi hệ thống: " + e.getMessage()).build();
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDtoResponse> getProductById(@PathVariable Integer id) {
        try {
            // Khi gọi hàm này, Service đã tự động chạy lệnh:
            // productMapper.incrementViewCount(id)
            ProductDtoResponse product = productService.findById(id);

            if (product == null) {
                return ApiResponse.<ProductDtoResponse>builder()
                        .success(false).statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy sản phẩm với ID: " + id).data(null).build();
            }
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true).statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin sản phẩm thành công").data(product).build();
        } catch (Exception e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage()).data(null).build();
        }
    }

    @GetMapping("/search/title")
    public ApiResponse<ProductDtoResponse> getProductByTitle(@RequestParam String title) {
        try {
            ProductDtoResponse product = productService.findByTitle(title);
            if (product == null) {
                return ApiResponse.<ProductDtoResponse>builder()
                        .success(false).statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy sản phẩm: " + title).data(null).build();
            }
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true).statusCode(HttpStatus.OK.value())
                    .message("Thành công").data(product).build();
        } catch (Exception e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage()).data(null).build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDtoResponse> createProduct(
            @ModelAttribute ProductDtoRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            ProductDtoResponse created = productService.createProduct(request, file);
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true).statusCode(HttpStatus.CREATED.value())
                    .message("Tạo sản phẩm thành công").data(created).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false).statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi tạo sản phẩm: " + e.getMessage()).data(null).build();
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDtoResponse> updateProduct(
            @PathVariable Integer id,
            @ModelAttribute ProductDtoRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            ProductDtoResponse updated = productService.updateProduct(id, request, file);
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(true).statusCode(HttpStatus.OK.value())
                    .message("Cập nhật sản phẩm thành công").data(updated).build();
        } catch (RuntimeException e) {
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false).statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage()).data(null).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<ProductDtoResponse>builder()
                    .success(false).statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi cập nhật: " + e.getMessage()).data(null).build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ApiResponse.<Void>builder()
                    .success(true).statusCode(HttpStatus.OK.value())
                    .message("Xóa sản phẩm thành công").data(null).build();
        } catch (RuntimeException e) {
            return ApiResponse.<Void>builder()
                    .success(false).statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage()).data(null).build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .success(false).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi xóa: " + e.getMessage()).data(null).build();
        }
    }

    @GetMapping("/bestsellers")
    public ApiResponse<List<ProductDtoResponse>> getBestSellers() {
        try {
            // Gọi Service lấy top bán chạy
            List<ProductDtoResponse> bestSellers = productService.getBestSellers();

            return ApiResponse.<List<ProductDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách sản phẩm bán chạy thành công")
                    .data(bestSellers)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/v1/products/bestsellers/paged
     * Lấy danh sách sản phẩm bán chạy với phân trang (max 50)
     */
    @GetMapping("/bestsellers/paged")
    public ApiResponse<PageResponse<ProductDtoResponse>> getBestSellersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            PageResponse<ProductDtoResponse> result = productService.getBestSellersPaged(page, size);

            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy sản phẩm bán chạy thành công")
                    .data(result)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/v1/products/search?keyword=nike&page=0&size=12
     * Tìm kiếm sản phẩm theo brand hoặc tên
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductDtoResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            PageResponse<ProductDtoResponse> result = productService.searchProducts(keyword, page, size);

            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Tìm kiếm sản phẩm thành công")
                    .data(result)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PageResponse<ProductDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

}