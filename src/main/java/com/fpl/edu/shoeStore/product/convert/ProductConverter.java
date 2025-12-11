package com.fpl.edu.shoeStore.product.convert;

import com.fpl.edu.shoeStore.product.dto.request.ProductDtoRequest;
import com.fpl.edu.shoeStore.product.dto.response.ProductDetailDtoResponse;
import com.fpl.edu.shoeStore.product.dto.response.ProductDtoResponse;
import com.fpl.edu.shoeStore.product.entity.Product;
import com.fpl.edu.shoeStore.product.entity.ProductVariant;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Converter - Convert giữa Entity và DTO
 */
public class ProductConverter {
    
    /**
     * Convert ProductDtoRequest → Product entity
     */
    public static Product toEntity(ProductDtoRequest dto) {
        return Product.builder()
                .categoryId(dto.getCategoryId())
                .name(dto.getName())
                .url(dto.getUrl())                      // Updated: slug → url
                .description(dto.getDescription())
                .productCode(dto.getProductCode())      // Updated: sku → productCode
                .basePrice(dto.getBasePrice())
                .isActive(dto.getIsActive())
                .build();
    }
    
    /**
     * Convert Product entity → ProductDtoResponse
     */
    public static ProductDtoResponse toResponse(Product entity) {
        return ProductDtoResponse.builder()
                .productId(entity.getProductId())
                .categoryId(entity.getCategoryId())
                .name(entity.getName())
                .url(entity.getUrl())                   // Updated: slug → url
                .description(entity.getDescription())
                .productCode(entity.getProductCode())   // Updated: sku → productCode
                .basePrice(entity.getBasePrice())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert Product + Variants → ProductDetailDtoResponse
     */
    public static ProductDetailDtoResponse toDetailResponse(Product product,
                                                           List<ProductVariant> variants) {
        return ProductDetailDtoResponse.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .url(product.getUrl())                  // Updated: slug → url
                .description(product.getDescription())
                .productCode(product.getProductCode())  // Updated: sku → productCode
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
