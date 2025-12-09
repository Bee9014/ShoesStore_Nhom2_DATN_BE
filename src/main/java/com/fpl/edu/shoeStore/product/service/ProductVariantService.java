  package com.fpl.edu.shoeStore.product.service;

     import java.util.List;

     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;

     public interface ProductVariantService {

         ProductVariantDtoResponse createVariant(ProductVariantDtoRequest request);

         ProductVariantDtoResponse updateVariant(Long variantId, ProductVariantDtoRequest request);

         void deleteVariant(Long variantId);

         ProductVariantDtoResponse getVariantById(Long variantId);

         List<ProductVariantDtoResponse> getVariantsByProductId(Long productId);

         List<ProductVariantDtoResponse> getActiveVariantsByProductId(Long productId);

         List<ProductVariantDtoResponse> getAllVariants();

         ProductVariantDtoResponse getVariantByCode(String code);

         void updateStock(Long variantId, Integer quantity);
     }
