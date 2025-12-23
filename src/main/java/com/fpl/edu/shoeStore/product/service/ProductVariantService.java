package com.fpl.edu.shoeStore.product.service;

     import java.util.List;

     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;

     public interface ProductVariantService {
         ProductVariantDtoResponse createVariant(ProductVariantDtoRequest request);
         void createVariants(Integer productId, List<ProductVariantDtoRequest> requests);

         ProductVariantDtoResponse updateVariant(Integer variantId, ProductVariantDtoRequest request);  // Đổi Long → Integer

         void deleteVariant(Integer variantId);                                                         // Đổi Long → Integer

         ProductVariantDtoResponse getVariantById(Integer variantId);                                   // Đổi Long → Integer

         List<ProductVariantDtoResponse> getVariantsByProductId(Integer productId);                     // Đổi Long → Integer

         List<ProductVariantDtoResponse> getAllVariants();

         ProductVariantDtoResponse getVariantByCode(String productVariantCode);

         void updateStock(Integer variantId, Integer quantity);                                         // Đổi Long → Integer

         // XÓA: getActiveVariantsByProductId
     }
