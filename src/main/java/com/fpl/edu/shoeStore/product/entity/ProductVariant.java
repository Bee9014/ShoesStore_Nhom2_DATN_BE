 package com.fpl.edu.shoeStore.product.entity;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     import java.time.LocalDate;

     @Data
     @Builder
     @NoArgsConstructor
     @AllArgsConstructor
     public class ProductVariant {
         private Integer variantId;       // INT trong DB
         private String skuCode;          // Đổi từ productVariantCode → skuCode
         private Double price;            // DECIMAL(18,2)
         private Integer qtyAvailable;    // Đổi từ stockQty → qtyAvailable
         private Integer weightGrams;     // THÊM MỚI
         private String attribute;        // THÊM MỚI (JSON string)
         private String image;            // THÊM MỚI (JSON array string)
         private LocalDate createAt;      // Đổi từ createdAt, kiểu DATE
         private LocalDate updateAt;      // Đổi từ updatedAt, kiểu DATE
         private Integer createBy;        // THÊM MỚI
         private Integer updateBy;        // THÊM MỚI
         private Integer productId;       // INT trong DB

         // XÓA: variantName, isActive
     }
