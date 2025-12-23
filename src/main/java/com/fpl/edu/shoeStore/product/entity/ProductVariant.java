 package com.fpl.edu.shoeStore.product.entity;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     import java.math.BigDecimal;
     import java.time.LocalDateTime;

     @Data
     @Builder
     @NoArgsConstructor
     @AllArgsConstructor
     public class ProductVariant {
         private Integer variantId;
         private Integer productId;
         private String variantName;          // ✅ ADDED - tên biến thể
         private String productVariantCode;
         private BigDecimal price;            // ✅ CHANGED - Double → BigDecimal
         private Integer stockQty;
         private Boolean isActive;            // ✅ ADDED - trạng thái
         private LocalDateTime createdAt;     // ✅ CHANGED - LocalDate → LocalDateTime
         private LocalDateTime updatedAt;     // ✅ CHANGED - LocalDate → LocalDateTime
         private Integer weightGrams;
         private String attribute;
         private String image;
         private Integer createBy;
         private Integer updateBy;
     }
