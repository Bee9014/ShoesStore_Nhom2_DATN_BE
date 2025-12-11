package com.fpl.edu.shoeStore.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Product DTO Response - Response sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDtoResponse {
    private Long productId;
    private Long categoryId;
    private String name;
    private String url;            // URL-friendly (was: slug)
    private String description;
    private String productCode;    // Mã sản phẩm (was: sku)
    private Double basePrice;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
