package com.fpl.edu.shoeStore.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // 👈 Import này
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // 👈 Import này
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; // 👈 Import này

import com.fpl.edu.shoeStore.common.handler.ApiResponse;
import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;
import com.fpl.edu.shoeStore.product.service.ProductVariantService;

import lombok.RequiredArgsConstructor;

/**
 * Product Variant REST Controller
 * Base URL: /api/product-variants
 */
@RestController
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    /**
     * POST /api/product-variants
     * Tạo biến thể mới (Hỗ trợ upload ảnh Multipart)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 👈 Quan trọng để nhận file
    public ApiResponse<ProductVariantDtoResponse> createVariant(
            @ModelAttribute ProductVariantDtoRequest request, // 👈 Dùng ModelAttribute thay vì RequestBody
            @RequestParam(value = "file", required = false) MultipartFile file // 👈 Nhận file ảnh
    ) {
        try {
            // Gọi service đã update để xử lý cả thông tin và file ảnh
            ProductVariantDtoResponse response = productVariantService.createVariant(request, file);
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Tạo product variant thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi tạo product variant: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * PUT /api/product-variants/{variantId}
     * Cập nhật thông tin biến thể (Hiện tại chưa hỗ trợ update ảnh riêng lẻ ở đây, chỉ update thông tin)
     */
    @PutMapping("/{variantId}")
    public ApiResponse<ProductVariantDtoResponse> updateVariant(
            @PathVariable Integer variantId,
            @RequestBody ProductVariantDtoRequest request) {
        try {
            ProductVariantDtoResponse response = productVariantService.updateVariant(variantId, request);
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật product variant thành công")
                    .data(response)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi cập nhật product variant: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * DELETE /api/product-variants/{variantId}
     */
    @DeleteMapping("/{variantId}")
    public ApiResponse<Void> deleteVariant(@PathVariable Integer variantId) {
        try {
            productVariantService.deleteVariant(variantId);
            return ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Xóa product variant thành công")
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
                    .message("Lỗi khi xóa product variant: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/product-variants/{variantId}
     */
    @GetMapping("/{variantId}")
    public ApiResponse<ProductVariantDtoResponse> getVariantById(@PathVariable Integer variantId) {
        try {
            ProductVariantDtoResponse response = productVariantService.getVariantById(variantId);
            if (response == null) {
                return ApiResponse.<ProductVariantDtoResponse>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy product variant với ID: " + variantId)
                        .data(null)
                        .build();
            }
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy thông tin product variant thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/product-variants/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductVariantDtoResponse>> getVariantsByProductId(@PathVariable Integer productId) {
        try {
            List<ProductVariantDtoResponse> responses = productVariantService.getVariantsByProductId(productId);
            return ApiResponse.<List<ProductVariantDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách variants theo product thành công")
                    .data(responses)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductVariantDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/product-variants
     */
    @GetMapping
    public ApiResponse<List<ProductVariantDtoResponse>> getAllVariants() {
        try {
            List<ProductVariantDtoResponse> responses = productVariantService.getAllVariants();
            return ApiResponse.<List<ProductVariantDtoResponse>>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Lấy danh sách tất cả variants thành công")
                    .data(responses)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductVariantDtoResponse>>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * GET /api/product-variants/code/{productVariantCode}
     */
    @GetMapping("/code/{productVariantCode}")
    public ApiResponse<ProductVariantDtoResponse> getVariantByCode(@PathVariable String productVariantCode) {
        try {
            ProductVariantDtoResponse response = productVariantService.getVariantByCode(productVariantCode);
            if (response == null) {
                return ApiResponse.<ProductVariantDtoResponse>builder()
                        .success(false)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Không tìm thấy product variant với code: " + productVariantCode)
                        .data(null)
                        .build();
            }
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Tìm kiếm variant theo code thành công")
                    .data(response)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ProductVariantDtoResponse>builder()
                    .success(false)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Lỗi: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * PATCH /api/product-variants/{variantId}/stock
     * Cập nhật số lượng tồn kho (Cộng/Trừ) - Có ghi log lịch sử
     */
    @PatchMapping("/{variantId}/stock")
    public ApiResponse<Void> updateStock(
            @PathVariable Integer variantId,
            @RequestParam Integer quantity) {
        try {
            productVariantService.updateStock(variantId, quantity);
            return ApiResponse.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Cập nhật stock thành công")
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi cập nhật stock: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}