  package com.fpl.edu.shoeStore.product.service.Impl;

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
         public ProductVariantDtoResponse updateVariant(Integer variantId, ProductVariantDtoRequest request) {  // Đổi Long →Integer
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
         public void deleteVariant(Integer variantId) {  // Đổi Long → Integer
             ProductVariant existingVariant = productVariantMapper.findById(variantId);
             if (existingVariant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }
             productVariantMapper.deleteById(variantId);
         }

         @Override
         public ProductVariantDtoResponse getVariantById(Integer variantId) {  // Đổi Long → Integer
             ProductVariant variant = productVariantMapper.findById(variantId);
             if (variant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }
             return ProductVariantConverter.toResponse(variant);
         }

         @Override
         public List<ProductVariantDtoResponse> getVariantsByProductId(Integer productId) {  // Đổi Long → Integer
             List<ProductVariant> variants = productVariantMapper.findByProductId(productId);
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
         public ProductVariantDtoResponse getVariantBySkuCode(String skuCode) {  // Đổi từ getVariantByCode
             ProductVariant variant = productVariantMapper.findBySkuCode(skuCode);  // Đổi từ findByCode
             if (variant == null) {
                 throw new RuntimeException("Product variant not found with sku code: " + skuCode);
             }
             return ProductVariantConverter.toResponse(variant);
         }

         @Override
         @Transactional
         public void updateStock(Integer variantId, Integer quantity) {  // Đổi Long → Integer
             ProductVariant existingVariant = productVariantMapper.findById(variantId);
             if (existingVariant == null) {
                 throw new RuntimeException("Product variant not found with id: " + variantId);
             }
             productVariantMapper.updateStock(variantId, quantity);
         }
     }
