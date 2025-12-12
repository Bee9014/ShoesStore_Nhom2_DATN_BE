 package com.fpl.edu.shoeStore.product.controller;

     import org.springframework.http.HttpStatus;
     import org.springframework.web.bind.annotation.DeleteMapping;
     import org.springframework.web.bind.annotation.GetMapping;
     import org.springframework.web.bind.annotation.PathVariable;
     import org.springframework.web.bind.annotation.PostMapping;
     import org.springframework.web.bind.annotation.PutMapping;
     import org.springframework.web.bind.annotation.RequestBody;
     import org.springframework.web.bind.annotation.RequestMapping;
     import org.springframework.web.bind.annotation.RequestParam;
     import org.springframework.web.bind.annotation.RestController;

     import com.fpl.edu.shoeStore.common.handler.ApiResponse;
     import com.fpl.edu.shoeStore.common.handler.PageResponse;
     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
     import com.fpl.edu.shoeStore.product.service.ProductService;

     import lombok.RequiredArgsConstructor;

     /**
      * Product REST Controller
      * Base URL: /api/v1/products
      *
      * Cập nhật theo database schema mới:
      * - title (thay vì name)
      * - brand, condition, defaultImage (trường mới)
      * - status (String thay vì Boolean isActive)
      * - Integer categoryId (thay vì Long)
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
          * @param title      Filter by product title (optional, partial match)
          * @param status     Filter by status: "active", "draft" (optional)
          * @param brand      Filter by brand name (optional)
          * @param page       Page number (default: 1)
          * @param size       Page size (default: 10)
          * @return ApiResponse with PageResponse of products
          */
         @GetMapping
         public ApiResponse<PageResponse<ProductDtoResponse>> getAllProducts(
                 @RequestParam(required = false) Integer categoryId,     // Đổi Long → Integer
                 @RequestParam(required = false) String title,           // Đổi từ name → title
                 @RequestParam(required = false) String status,          // Đổi từ Boolean isActive → String status
                 @RequestParam(defaultValue = "1") int page,
                 @RequestParam(defaultValue = "10") int size
         ) {
             try {
                 PageResponse<ProductDtoResponse> pageResponse = productService.findAllPaged(
                         categoryId, title, status, page, size           // Cập nhật tham số
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
                         .message("Lỗi khi lấy thông tin sản phẩm: " + e.getMessage())
                         .data(null)
                         .build();
             }
         }

         /**
          * GET /api/v1/products/search/title - Search product by exact title
          *
          * @param title Product title
          * @return ApiResponse with product data
          */
         @GetMapping("/search/title")                                        // Đổi từ /search/name
         public ApiResponse<ProductDtoResponse> getProductByTitle(@RequestParam String title) {  // Đổi từ name → title
             try {
                 ProductDtoResponse product = productService.findByTitle(title);  // Đổi từ findByName

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
                         .message("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage())
                         .data(null)
                         .build();
             }
         }

         /**
          * POST /api/v1/products - Create new product
          *
          * Request body example:
          * {
          *   "categoryId": 101,
          *   "title": "Giày Nike Air Max 2024",
          *   "description": "Giày thể thao cao cấp...",
          *   "brand": "Nike",
          *   "condition": "New",
          *   "defaultImage": "/images/nike-air-max.jpg",
          *   "status": "active",
          *   "createBy": 1,
          *   "updateBy": 1
          * }
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
          * Request body example:
          * {
          *   "title": "Giày Nike Air Max 2024 - Updated",
          *   "description": "Mô tả cập nhật...",
          *   "brand": "Nike",
          *   "condition": "New",
          *   "defaultImage": "/images/nike-air-max-new.jpg",
          *   "status": "active",
          *   "updateBy": 1
          * }
          *
          * @param id      Product ID
          * @param request Updated product data
          * @return ApiResponse with updated product
          */
         @PutMapping("/{id}")
         public ApiResponse<ProductDtoResponse> updateProduct(
                 @PathVariable Integer id,
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
          * DELETE /api/v1/products/{id} - Delete product
          *
          * @param id Product ID
          * @return ApiResponse with success message
          */
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
