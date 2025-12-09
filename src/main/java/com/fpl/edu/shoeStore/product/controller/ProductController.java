package com.fpl.edu.shoeStore.product.controller;

     import org.springframework.http.HttpStatus;
     import org.springframework.http.ResponseEntity;
     import org.springframework.web.bind.annotation.*;

     import com.fpl.edu.shoeStore.common.handler.PageResponse;
     import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
     import com.fpl.edu.shoeStore.product.service.ProductService;

     import lombok.RequiredArgsConstructor;

     @RestController
     @RequestMapping("/api/v1/products")
     @RequiredArgsConstructor
     public class ProductController {

         private final ProductService productService;

         @GetMapping
         public ResponseEntity<PageResponse<ProductDtoResponse>> getAllProducts(
                 @RequestParam(required = false) Long categoryId,
                 @RequestParam(required = false) String name,
                 @RequestParam(required = false) String slug,
                 @RequestParam(required = false) Boolean isActive,
                 @RequestParam(defaultValue = "1") int page,
                 @RequestParam(defaultValue = "10") int size
         ) {
             PageResponse<ProductDtoResponse> response = productService.findAllPaged(
                     categoryId, name, slug, isActive, page, size
             );
             return ResponseEntity.ok(response);
         }

         @GetMapping("/{id}")
         public ResponseEntity<ProductDtoResponse> getProductById(@PathVariable Long id) {
             ProductDtoResponse product = productService.findById(id);
             if (product == null) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
             }
             return ResponseEntity.ok(product);
         }

         @GetMapping("/search/name")
         public ResponseEntity<ProductDtoResponse> getProductByName(@RequestParam String name) {
             ProductDtoResponse product = productService.findByName(name);
             if (product == null) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
             }
             return ResponseEntity.ok(product);
         }

         @PostMapping
         public ResponseEntity<ProductDtoResponse> createProduct(@RequestBody ProductDtoRequest request) {
             ProductDtoResponse created = productService.createProduct(request);
             return ResponseEntity.status(HttpStatus.CREATED).body(created);
         }

         @PutMapping("/{id}")
         public ResponseEntity<ProductDtoResponse> updateProduct(
                 @PathVariable Long id,
                 @RequestBody ProductDtoRequest request)
          {
             try {
                 ProductDtoResponse updated = productService.updateProduct(id, request);
                 return ResponseEntity.ok(updated);
             } catch (RuntimeException e) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
             }
         }

         @DeleteMapping("/{id}")
         public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
             try {
                 productService.deleteProduct(id);
                 return ResponseEntity.noContent().build();
             } catch (RuntimeException e) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
             }
         }
     }