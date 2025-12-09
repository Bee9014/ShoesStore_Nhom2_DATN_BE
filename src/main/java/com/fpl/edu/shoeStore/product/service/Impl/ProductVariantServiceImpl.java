 package com.fpl.edu.shoeStore.product.service.impl;

     import java.util.List;
     import java.util.stream.Collectors;

     import org.springframework.stereotype.Service;
     import org.springframework.transaction.annotation.Transactional;

     import com.fpl.edu.shoeStore.product.convert.ProductVariantConverter;
     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;
     import com.fpl.edu.shoeStore.product.entity.ProductVariant;
     import com.fpl.edu.shoeStore.product.mapper.ProductVariantMapper;
     import com.fpl.edu.shoeStore.product.service.ProductVariantService;

     import lombok.RequiredArgsConstructor;

     @Service
     @RequiredArgsConstructor
     public class ProductVariantServiceImpl implements ProductVariantService {

         private final ProductVariantMapper productVariantMapper;

         @Override
         @Transactional
         public ProductVariantDtoResponse createVariant(ProductVariantDtoRequest request) {
             ProductVariant variant = ProductVariantConverter.toEntity(request);
             productVariantMapper.insert(variant);
             return ProductVariantConverter.toResponse(variant);
         }

         @Override
         @Transactional
         public ProductVariantDtoResponse updateVariant(Long variantId, ProductVariantDtoRequest request) {
             ProductVariant existingVariant = productVariantMapper.findById(variantId);
             if (existingVariant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }

             ProductVariant variant = ProductVariantConverter.toEntity(request);
             variant.setVariantId(variantId);
             productVariantMapper.update(variant);

             return ProductVariantConverter.toResponse(productVariantMapper.findById(variantId));
         }

         @Override
         @Transactional
         public void deleteVariant(Long variantId) {
             ProductVariant existingVariant = productVariantMapper.findById(variantId);
             if (existingVariant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }
             productVariantMapper.deleteById(variantId);
         }

         @Override
         public ProductVariantDtoResponse getVariantById(Long variantId) {
             ProductVariant variant = productVariantMapper.findById(variantId);
             if (variant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }
             return ProductVariantConverter.toResponse(variant);
         }

         @Override
         public List<ProductVariantDtoResponse> getVariantsByProductId(Long productId) {
             List<ProductVariant> variants = productVariantMapper.findByProductId(productId);
             return variants.stream()
                     .map(ProductVariantConverter::toResponse)
                     .collect(Collectors.toList());
         }

         @Override
         public List<ProductVariantDtoResponse> getActiveVariantsByProductId(Long productId) {
             List<ProductVariant> variants = productVariantMapper.findActiveByProductId(productId);
             return variants.stream()
                     .map(ProductVariantConverter::toResponse)
                     .collect(Collectors.toList());
         }

         @Override
         public List<ProductVariantDtoResponse> getAllVariants() {
             List<ProductVariant> variants = productVariantMapper.findAll();
             return variants.stream()
                     .map(ProductVariantConverter::toResponse)
                     .collect(Collectors.toList());
         }

         @Override
         public ProductVariantDtoResponse getVariantByCode(String code) {
             ProductVariant variant = productVariantMapper.findByCode(code);
             if (variant == null) {
                 throw new RuntimeException("Product variant not found with code: " + code);
             }
             return ProductVariantConverter.toResponse(variant);
         }

         @Override
         @Transactional
         public void updateStock(Long variantId, Integer quantity) {
             ProductVariant existingVariant = productVariantMapper.findById(variantId);
             if (existingVariant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }
             productVariantMapper.updateStock(variantId, quantity);
         }
     }
