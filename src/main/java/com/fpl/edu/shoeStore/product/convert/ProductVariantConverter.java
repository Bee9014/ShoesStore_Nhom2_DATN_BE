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
                     .price(dto.getPrice() != null ? dto.getPrice() : null)
                     .stockQty(dto.getStockQty())
                     .isActive(dto.getIsActive())
                     .weightGrams(dto.getWeightGrams())
                     .size(dto.getSize())
                     .color(dto.getColor())
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
                     .price(entity.getPrice() != null ? entity.getPrice() : null)
                     .stockQty(entity.getStockQty())
                     .isActive(entity.getIsActive())
                     .weightGrams(entity.getWeightGrams())
                     .size(entity.getSize())
                     .color(entity.getColor())
                     .image(entity.getImage())
                     .createAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : null)
                     .updateAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                     .createBy(entity.getCreateBy())
                     .updateBy(entity.getUpdateBy())
                     .build();
         }
     }
