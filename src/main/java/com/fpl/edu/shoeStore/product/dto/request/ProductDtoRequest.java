package com.fpl.edu.shoeStore.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product DTO Request - Tạo/Cập nhật sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDtoRequest {
    private Long categoryId;
    private String name;
    private String url;            // URL-friendly (was: slug)
    private String description;
    private String productCode;    // Mã sản phẩm (was: sku)
    private Double basePrice;
    private Boolean isActive;
}
