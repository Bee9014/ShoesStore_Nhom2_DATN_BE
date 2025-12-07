package com.fpl.edu.shoeStore.product.convert;
import com.fpl.edu.shoeStore.product.entity.ProductVariant;
import com.fpl.edu.shoeStore.product.dto.request.ProductVariantDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductVariantDtoResponse;


public class ProductVariantConverter {
    public static ProductVariant toEntity(ProductVariantDtoRequest dto) {
        return ProductVariant.builder()
                .productId(dto.getProductId())
                .variantName(dto.getVariantName())
                .productVariantCode(dto.getProductVariantCode())
                .price(dto.getPrice())
                .stockQty(dto.getStockQty())
                .isActive(dto.getIsActive())
                .build();
    }

    public static ProductVariantDtoResponse toResponse(ProductVariant entity) {
        return ProductVariantDtoResponse.builder()
                .variantId(entity.getVariantId())
                .productId(entity.getProductId())
                .variantName(entity.getVariantName())
                .productVariantCode(entity.getProductVariantCode())
                .price(entity.getPrice())
                .stockQty(entity.getStockQty())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}