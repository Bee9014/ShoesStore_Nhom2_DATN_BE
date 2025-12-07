package com.fpl.edu.shoeStore.product.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductVariant {
    private Long variantId;              // variant_id
    private Long productId;              // product_id
    private String variantName;          // variant_name (Size 40, Màu Đen - Size 39)
    private String productVariantCode;   // product_variant_code (SKU)
    private Double price;                // price
    private Integer stockQty;            // stock_qty
    private Boolean isActive;            // is_active
    private LocalDateTime createdAt;     // created_at
    private LocalDateTime updatedAt;     // updated_at
}
