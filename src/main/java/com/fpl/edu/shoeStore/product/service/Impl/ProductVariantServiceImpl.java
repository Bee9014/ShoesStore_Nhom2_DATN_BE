  package com.fpl.edu.shoeStore.product.service.impl;

     import java.util.List;
import java.util.UUID;
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
    public void createVariants(Integer productId, List<ProductVariantDtoRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        for (ProductVariantDtoRequest req : requests) {
            ProductVariant variant = ProductVariantConverter.toEntity(req);
            
            // 1. Gán khóa ngoại (ID của Product cha)
            variant.setProductId(productId);

            // 2. Tự động sinh mã SKU nếu Frontend không gửi lên
            if (variant.getProductVariantCode() == null || variant.getProductVariantCode().trim().isEmpty()) {
                // Ví dụ: PVC-17150000-AB12
                String autoCode = "PVC-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                variant.setProductVariantCode(autoCode);
            }
            
            // 3. Set mặc định người tạo (nếu chưa có)
            if (variant.getCreateBy() == null) {
                variant.setCreateBy(1); // Mặc định admin ID 1 hoặc lấy từ context
            }
            if (variant.getUpdateBy() == null) {
                variant.setUpdateBy(1);
            }

            // 4. Lưu vào DB
            productVariantMapper.insert(variant);
        }
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
             variant.setProductId(existingVariant.getProductId());
             productVariantMapper.update(variant);
             if (variant.getProductVariantCode() == null) {
        variant.setProductVariantCode(existingVariant.getProductVariantCode());
    }

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
         public ProductVariantDtoResponse getVariantByCode(String productVariantCode) {
             ProductVariant variant = productVariantMapper.findByCode(productVariantCode);
             if (variant == null) {
                 throw new RuntimeException("Product variant not found with code: " + productVariantCode);
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
