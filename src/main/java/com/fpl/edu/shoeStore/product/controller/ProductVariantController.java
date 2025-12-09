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

     @RestController
     @RequestMapping("/api/product-variants")
     @RequiredArgsConstructor
     public class ProductVariantController {

         private final ProductVariantService productVariantService;

         @PostMapping
         public ResponseEntity<ProductVariantDtoResponse> createVariant(@RequestBody ProductVariantDtoRequest request) {
             ProductVariantDtoResponse response = productVariantService.createVariant(request);
             return new ResponseEntity<>(response, HttpStatus.CREATED);
         }

         @PutMapping("/{variantId}")
         public ResponseEntity<ProductVariantDtoResponse> updateVariant(
                 @PathVariable Long variantId,
                 @RequestBody ProductVariantDtoRequest request) {
             ProductVariantDtoResponse response = productVariantService.updateVariant(variantId, request);
             return ResponseEntity.ok(response);
         }

         @DeleteMapping("/{variantId}")
         public ResponseEntity<Void> deleteVariant(@PathVariable Long variantId) {
             productVariantService.deleteVariant(variantId);
             return ResponseEntity.noContent().build();
         }

         @GetMapping("/{variantId}")
         public ResponseEntity<ProductVariantDtoResponse> getVariantById(@PathVariable Long variantId) {
             ProductVariantDtoResponse response = productVariantService.getVariantById(variantId);
             return ResponseEntity.ok(response);
         }

         @GetMapping("/product/{productId}")
         public ResponseEntity<List<ProductVariantDtoResponse>> getVariantsByProductId(@PathVariable Long productId) {
             List<ProductVariantDtoResponse> responses = productVariantService.getVariantsByProductId(productId);
             return ResponseEntity.ok(responses);
         }

         @GetMapping("/product/{productId}/active")
         public ResponseEntity<List<ProductVariantDtoResponse>> getActiveVariantsByProductId(@PathVariable Long
     productId) {
             List<ProductVariantDtoResponse> responses = productVariantService.getActiveVariantsByProductId(productId);
             return ResponseEntity.ok(responses);
         }

         @GetMapping
         public ResponseEntity<List<ProductVariantDtoResponse>> getAllVariants() {
             List<ProductVariantDtoResponse> responses = productVariantService.getAllVariants();
             return ResponseEntity.ok(responses);
         }

         @GetMapping("/code/{code}")
         public ResponseEntity<ProductVariantDtoResponse> getVariantByCode(@PathVariable String code) {
             ProductVariantDtoResponse response = productVariantService.getVariantByCode(code);
             return ResponseEntity.ok(response);
         }

         @PatchMapping("/{variantId}/stock")
         public ResponseEntity<Void> updateStock(
                 @PathVariable Long variantId,
                 @RequestParam Integer quantity) {
             productVariantService.updateStock(variantId, quantity);
             return ResponseEntity.ok().build();
         }
     }