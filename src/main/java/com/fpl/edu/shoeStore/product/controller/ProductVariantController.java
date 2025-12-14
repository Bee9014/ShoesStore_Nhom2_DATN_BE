package com.fpl.edu.shoeStore.product.controller;

     import java.util.List;

     import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
     import org.springframework.web.bind.annotation.PatchMapping;
     import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
     import org.springframework.web.bind.annotation.PutMapping;
     import org.springframework.web.bind.annotation.RequestBody;
     import org.springframework.web.bind.annotation.RequestMapping;
     import org.springframework.web.bind.annotation.RequestParam;
     import org.springframework.web.bind.annotation.RestController;

     import com.fpl.edu.shoeStore.common.handler.ApiResponse;
     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;
     import com.fpl.edu.shoeStore.product.service.ProductVariantService;

     import lombok.RequiredArgsConstructor;

     /**
      * Product Variant REST Controller
      * Base URL: /api/product-variants
      *
      * Cập nhật theo database schema mới:
      * - productVariantCode (VARCHAR 100)
      * - stockQty (INT)
      * - weightGrams, attribute, image (JSON fields)
      * - Integer cho tất cả IDs
      */
     @RestController
     @RequestMapping("/api/product-variants")
     @RequiredArgsConstructor
     public class ProductVariantController {

         private final ProductVariantService productVariantService;
         /**
          * POST /api/product-variants - Create new product variant
          *
          * Request body example:
          * {
          *   "productId": 1,
          *   "productVariantCode": "NIKE-AM-BLK-40",
          *   "price": 2500000.00,
          *   "stockQty": 50,
          *   "weightGrams": 850,
          *   "attribute": "{\"Size\": 40, \"Color\": \"Black\"}",
          *   "image": "[\"/images/1/black-40.jpg\", \"/images/1/black-40-2.jpg\"]",
          *   "createBy": 1,
          *   "updateBy": 1
          * }
          *
          * @param request Product variant data
          * @return Created product variant
          */
       @PostMapping
     public ApiResponse<ProductVariantDtoResponse>
     createVariant(@RequestBody ProductVariantDtoRequest request) {
         try {
             ProductVariantDtoResponse response = productVariantService.createVariant(request);
             return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(true)
                     .statusCode(HttpStatus.CREATED.value())
                     .message("Tạo product variant thành công")
                     .data(response)
                     .build();
         } catch (Exception e) {
             return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(false)
                     .statusCode(HttpStatus.BAD_REQUEST.value())        
                     .message("Lỗi khi tạo product variant: " + e.getMessage())
                     .data(null)
                     .build();
         }
     }

         /**
          * PUT /api/product-variants/{variantId} - Update product variant
          *
          * @param variantId Variant ID (Integer)
          * @param request   Updated variant data
          * @return Updated product variant
          */
         @PutMapping("/{variantId}")
     public ApiResponse<ProductVariantDtoResponse>updateVariant(
             @PathVariable Integer variantId,
             @RequestBody ProductVariantDtoRequest request) {
         try {
             ProductVariantDtoResponse response = productVariantService.updateVariant(variantId, request);
             return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(true)
                     .statusCode(HttpStatus.OK.value())
                     .message("Cập nhật product variant thành công")
                     .data(response)
                     .build();
         } catch (RuntimeException e) {
             return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(false)
                     .statusCode(HttpStatus.NOT_FOUND.value())
                     .message(e.getMessage())
                     .data(null)
                     .build();
         } catch (Exception e) {
             return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(false)
                     .statusCode(HttpStatus.BAD_REQUEST.value())        
                     .message("Lỗi khi cập nhật product variant:" + e.getMessage())
                     .data(null)
                     .build();
         }
     }

         /**
          * DELETE /api/product-variants/{variantId} - Delete product variant
          *
          * @param variantId Variant ID (Integer)
          * @return 204 No Content
          */
         @DeleteMapping("/{variantId}")
     public ApiResponse<Void> deleteVariant(@PathVariable  Integer variantId) {
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
          * GET /api/product-variants/{variantId} - Get product variant by ID
          *
          * @param variantId Variant ID (Integer)
          * @return Product variant data
          */
          @GetMapping("/{variantId}")
     public ApiResponse<ProductVariantDtoResponse> getVariantById(@PathVariable Integer variantId) {
         try {
             ProductVariantDtoResponse response = productVariantService.getVariantById(variantId);

             if (response == null) {
                 return
     ApiResponse.<ProductVariantDtoResponse>builder()
                         .success(false)
                         .statusCode(HttpStatus.NOT_FOUND.value())
                         .message("Không tìm thấy product variant với ID: " + variantId)
                         .data(null)
                         .build();
             }
   return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(true)
                     .statusCode(HttpStatus.OK.value())
                     .message("Lấy thông tin product variant thành công")
                     .data(response)
                     .build();
         } catch (Exception e) {
             return
     ApiResponse.<ProductVariantDtoResponse>builder()
                     .success(false)

     .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                     .message("Lỗi khi lấy thông tin product variant: " + e.getMessage())
                     .data(null)
                     .build();
         }
     }


         /**
          * GET /api/product-variants/product/{productId} - Get all variants of a product
          *
          * @param productId Product ID (Integer)
          * @return List of product variants
          */
          @GetMapping("/product/{productId}")
     public ApiResponse<List<ProductVariantDtoResponse>>
     getVariantsByProductId(
             @PathVariable Integer productId) {
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
                     .message("Lỗi khi lấy danh sách variants: " + e.getMessage())
                     .data(null)
                     .build();
         }
     }

         /**
          * GET /api/product-variants - Get all product variants
          *
          * @return List of all product variants
          */
         @GetMapping
     public ApiResponse<List<ProductVariantDtoResponse>> getAllVariants() {
         try {
             List<ProductVariantDtoResponse> responses =
     productVariantService.getAllVariants();
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
                     .message("Lỗi khi lấy danh sách variants: " + e.getMessage())
                     .data(null)
                     .build();
         }
     }

         /**
          * GET /api/product-variants/code/{productVariantCode} - Get variant by code
          *
          * Example: GET /api/product-variants/code/NIKE-AM-BLK-40
          *
          * @param productVariantCode Product variant code (unique identifier)
          * @return Product variant data
          */
        @GetMapping("/code/{productVariantCode}")
     public ApiResponse<ProductVariantDtoResponse>
     getVariantByCode(@PathVariable String productVariantCode) {
         try {
             ProductVariantDtoResponse response =
     productVariantService.getVariantByCode(productVariantCode);

             if (response == null) {
                 return ApiResponse.<ProductVariantDtoResponse>builder()
                         .success(false)
                         .statusCode(HttpStatus.NOT_FOUND.value())
                         .message("Không tìm thấy product variant với code: "+ productVariantCode)
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
                     .message("Lỗi khi tìm kiếm variant: " + e.getMessage())
                     .data(null)
                     .build();
         }
     }
         /**
          * PATCH /api/product-variants/{variantId}/stock - Update stock quantity
          *
          * Example: PATCH /api/product-variants/101/stock?quantity=-5
          * (Giảm 5 sản phẩm khi bán hàng)
          *
          * Example: PATCH /api/product-variants/101/stock?quantity=10
          * (Tăng 10 sản phẩm khi nhập hàng)
          *
          * @param variantId Variant ID (Integer)
          * @param quantity  Quantity to add (positive) or subtract (negative)
          * @return 200 OK
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


         // ====================================================================
         // XÓA METHOD: getActiveVariantsByProductId
         // Lý do: Database không có cột is_active cho product_variant
         // ====================================================================

         /*
         @GetMapping("/product/{productId}/active")
         public ResponseEntity<List<ProductVariantDtoResponse>> getActiveVariantsByProductId(@PathVariable Integer productId) {
             // METHOD NÀY ĐÃ BỊ XÓA - không còn cột is_active trong DB
         }
         */
     }
