package com.fpl.edu.shoeStore.product.controller;

     import java.util.List;

     import org.springframework.http.HttpStatus;
     import org.springframework.http.ResponseEntity;
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

     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;
     import com.fpl.edu.shoeStore.product.service.ProductVariantService;

     import lombok.RequiredArgsConstructor;

     /**
      * Product Variant REST Controller
      * Base URL: /api/product-variants
      *
      * Cập nhật theo database schema mới:
      * - skuCode (thay vì productVariantCode)
      * - qtyAvailable (thay vì stockQty)
      * - weightGrams, attribute, image (trường mới)
      * - Integer cho tất cả IDs (thay vì Long)
      * - Xóa isActive và các active-related methods
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
          *   "skuCode": "NIKE-AM-BLK-40",
          *   "price": 2500000.00,
          *   "qtyAvailable": 50,
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
         public ResponseEntity<ProductVariantDtoResponse> createVariant(@RequestBody ProductVariantDtoRequest request) {
             ProductVariantDtoResponse response = productVariantService.createVariant(request);
             return new ResponseEntity<>(response, HttpStatus.CREATED);
         }

         /**
          * PUT /api/product-variants/{variantId} - Update product variant
          *
          * @param variantId Variant ID (Integer)
          * @param request   Updated variant data
          * @return Updated product variant
          */
         @PutMapping("/{variantId}")
         public ResponseEntity<ProductVariantDtoResponse> updateVariant(
                 @PathVariable Integer variantId,                            // Đổi Long → Integer
                 @RequestBody ProductVariantDtoRequest request) {
             ProductVariantDtoResponse response = productVariantService.updateVariant(variantId, request);
             return ResponseEntity.ok(response);
         }

         /**
          * DELETE /api/product-variants/{variantId} - Delete product variant
          *
          * @param variantId Variant ID (Integer)
          * @return 204 No Content
          */
         @DeleteMapping("/{variantId}")
         public ResponseEntity<Void> deleteVariant(@PathVariable Integer variantId) {  // Đổi Long → Integer
             productVariantService.deleteVariant(variantId);
             return ResponseEntity.noContent().build();
         }

         /**
          * GET /api/product-variants/{variantId} - Get product variant by ID
          *
          * @param variantId Variant ID (Integer)
          * @return Product variant data
          */
         @GetMapping("/{variantId}")
         public ResponseEntity<ProductVariantDtoResponse> getVariantById(@PathVariable Integer variantId) {  // Đổi Long →Integer
             ProductVariantDtoResponse response = productVariantService.getVariantById(variantId);
             return ResponseEntity.ok(response);
         }

         /**
          * GET /api/product-variants/product/{productId} - Get all variants of a product
          *
          * @param productId Product ID (Integer)
          * @return List of product variants
          */
         @GetMapping("/product/{productId}")
         public ResponseEntity<List<ProductVariantDtoResponse>> getVariantsByProductId(
                 @PathVariable Integer productId) {                          // Đổi Long → Integer
             List<ProductVariantDtoResponse> responses = productVariantService.getVariantsByProductId(productId);
             return ResponseEntity.ok(responses);
         }

         /**
          * GET /api/product-variants - Get all product variants
          *
          * @return List of all product variants
          */
         @GetMapping
         public ResponseEntity<List<ProductVariantDtoResponse>> getAllVariants() {
             List<ProductVariantDtoResponse> responses = productVariantService.getAllVariants();
             return ResponseEntity.ok(responses);
         }

         /**
          * GET /api/product-variants/sku/{skuCode} - Get variant by SKU code
          *
          * Example: GET /api/product-variants/sku/NIKE-AM-BLK-40
          *
          * @param skuCode SKU code (unique identifier)
          * @return Product variant data
          */
         @GetMapping("/sku/{skuCode}")                                       // Đổi từ /code/{code} → /sku/{skuCode}
         public ResponseEntity<ProductVariantDtoResponse> getVariantBySkuCode(@PathVariable String skuCode) {  // Đổi từ code →skuCode
             ProductVariantDtoResponse response = productVariantService.getVariantBySkuCode(skuCode);  // Đổi từ getVariantByCode
             return ResponseEntity.ok(response);
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
         public ResponseEntity<Void> updateStock(
                 @PathVariable Integer variantId,                            // Đổi Long → Integer
                 @RequestParam Integer quantity) {
             productVariantService.updateStock(variantId, quantity);
             return ResponseEntity.ok().build();
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
