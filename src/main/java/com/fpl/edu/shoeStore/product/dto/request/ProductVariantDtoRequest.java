package com.fpl.edu.shoeStore.product.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductVariantDtoRequest {
    private Long productId;
    private String variantName;          // VD: "Size 41", "Màu Xanh - Size 42"
    private String productVariantCode;   // SKU code
    private Double price;
    private Integer stockQty;
    private Boolean isActive;
}