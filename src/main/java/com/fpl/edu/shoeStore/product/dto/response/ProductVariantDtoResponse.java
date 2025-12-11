package com.fpl.edu.shoeStore.product.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDtoResponse {
    private Long variantId;
    private Long productId;
    private String variantName;
    private String productVariantCode;
    private Double price;
    private Integer stockQty;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

