package com.fpl.edu.shoeStore.product.dto.response;

import java.math.BigDecimal;
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
    private Integer variantId;
    private Integer productId;
    private String productVariantCode;
    private BigDecimal price;
    private Integer stockQty;
    private Boolean isActive;
    private Integer weightGrams;
    private String size;
    private String color;
    private String image;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Integer createBy;
    private Integer updateBy;
}