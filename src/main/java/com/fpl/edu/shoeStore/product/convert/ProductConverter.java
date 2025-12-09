package com.fpl.edu.shoeStore.product.convert;

import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDetailDtoResponse;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.entity.ProductVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ProductConverter {
    public static Product toEntity(ProductDtoRequest dto) {
        return Product.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .basePrice(dto.getBasePrice())
                .isActive(dto.getIsActive())
                .build();
    }
    public static ProductDtoResponse toResponse(Product entity) {
        return ProductDtoResponse.builder()
                .productId(entity.getProductId())
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .basePrice(entity.getBasePrice())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

     public static ProductDetailDtoResponse toDetailResponse(Product product,
     List<ProductVariant> variants) {
         return ProductDetailDtoResponse.builder()
                 .productId(product.getProductId())
                 .categoryId(product.getCategoryId())
                 .name(product.getName())
                 .slug(product.getSlug())
                 .description(product.getDescription())
                 .sku(product.getSku())
                 .basePrice(product.getBasePrice())
                 .isActive(product.getIsActive())
                 .createdAt(product.getCreatedAt())
                 .updatedAt(product.getUpdatedAt())
                 .variants(variants.stream()
                         .map(ProductVariantConverter::toResponse)
                         .collect(Collectors.toList()))
                 .build();
     }
}
