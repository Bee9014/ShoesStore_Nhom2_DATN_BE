 package com.fpl.edu.shoeStore.product.dto.response;

     import java.time.LocalDate;
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
         private String skuCode;        // Đổi từ productVariantCode → skuCode
         private Double price;
         private Integer qtyAvailable;  // Đổi từ stockQty → qtyAvailable
         private Integer weightGrams;   // THÊM MỚI
         private String attribute;      // THÊM MỚI
         private String image;          // THÊM MỚI
         private LocalDate createAt;    // Đổi từ createdAt
         private LocalDate updateAt;    // Đổi từ updatedAt
         private Integer createBy;      // THÊM MỚI
         private Integer updateBy;      // THÊM MỚI

         // XÓA: variantName, isActive
     }