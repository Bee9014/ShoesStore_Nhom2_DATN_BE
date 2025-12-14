 package com.fpl.edu.shoeStore.product.convert;

     import com.fpl.edu.shoeStore.product.entity.ProductVariant;
     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;

     public class ProductVariantConverter {

         public static ProductVariant toEntity(ProductVariantDtoRequest dto) {
             return ProductVariant.builder()
                     .productId(dto.getProductId())
                     .productVariantCode(dto.getProductVariantCode())
                     .price(dto.getPrice())
                     .stockQty(dto.getStockQty())
                     .weightGrams(dto.getWeightGrams())
                     .attribute(dto.getAttribute())
                     .image(dto.getImage())
                     .createBy(dto.getCreateBy())
                     .updateBy(dto.getUpdateBy())
                     .build();
         }

         public static ProductVariantDtoResponse toResponse(ProductVariant entity) {
             return ProductVariantDtoResponse.builder()
                     .variantId(entity.getVariantId())
                     .productId(entity.getProductId())
                     .productVariantCode(entity.getProductVariantCode())
                     .price(entity.getPrice())
                     .stockQty(entity.getStockQty())
                     .weightGrams(entity.getWeightGrams())
                     .attribute(entity.getAttribute())
                     .image(entity.getImage())
                     .createAt(entity.getCreateAt())
                     .updateAt(entity.getUpdateAt())
                     .createBy(entity.getCreateBy())
                     .updateBy(entity.getUpdateBy())
                     .build();
         }
     }
