 package com.fpl.edu.shoeStore.product.convert;

     import com.fpl.edu.shoeStore.product.entity.ProductVariant;
     import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
     import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;

     import java.math.BigDecimal;

     public class ProductVariantConverter {

         public static ProductVariant toEntity(ProductVariantDtoRequest dto) {
             return ProductVariant.builder()
                     .productId(dto.getProductId())
                     .productVariantCode(dto.getProductVariantCode())
                     .price(dto.getPrice() != null ? BigDecimal.valueOf(dto.getPrice()) : null)
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
                     .price(entity.getPrice() != null ? entity.getPrice().doubleValue() : null)
                     .stockQty(entity.getStockQty())
                     .weightGrams(entity.getWeightGrams())
                     .attribute(entity.getAttribute())
                     .image(entity.getImage())
                     .createAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDate() : null)
                     .updateAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toLocalDate() : null)
                     .createBy(entity.getCreateBy())
                     .updateBy(entity.getUpdateBy())
                     .build();
         }
     }
