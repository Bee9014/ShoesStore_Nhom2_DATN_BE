package com.fpl.edu.shoeStore.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Product Entity - Sản phẩm chính
 * Maps to: products table
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long productId;
    private Long categoryId;
    private String name;
    private String url;              // URL-friendly (was: slug)
    private String description;
    private String productCode;      // Mã sản phẩm (was: sku)
    private Double basePrice;        // Giá tham chiếu
    private Boolean isActive;        // 1=Đang bán, 0=Ngừng bán
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
