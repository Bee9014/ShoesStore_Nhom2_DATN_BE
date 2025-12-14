package com.fpl.edu.shoeStore.product.service;

     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;

     import java.util.List;

     public interface ProductVariantService {
         ProductVariantDtoResponse createVariant(ProductVariantDtoRequest request);

         ProductVariantDtoResponse updateVariant(Integer variantId, ProductVariantDtoRequest request);  // Đổi Long → Integer

         void deleteVariant(Integer variantId);                                                         // Đổi Long → Integer

         ProductVariantDtoResponse getVariantById(Integer variantId);                                   // Đổi Long → Integer

         List<ProductVariantDtoResponse> getVariantsByProductId(Integer productId);                     // Đổi Long → Integer

         List<ProductVariantDtoResponse> getAllVariants();

         ProductVariantDtoResponse getVariantByCode(String productVariantCode);

         void updateStock(Integer variantId, Integer quantity);                                         // Đổi Long → Integer

         // XÓA: getActiveVariantsByProductId
     }
