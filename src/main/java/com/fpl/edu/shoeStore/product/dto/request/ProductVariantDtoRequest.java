 package com.fpl.edu.shoeStore.product.dto.request;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class ProductVariantDtoRequest {
         private Integer productId;
         private String skuCode;        // Đổi từ productVariantCode → skuCode
         private Double price;
         private Integer qtyAvailable;  // Đổi từ stockQty → qtyAvailable
         private Integer weightGrams;   // THÊM MỚI
         private String attribute;      // THÊM MỚI (JSON format: {"Size": 40, "Color": "Black"})
         private String image;          // THÊM MỚI (JSON array: ["/images/1/v1.jpg"])
         private Integer createBy;      // THÊM MỚI
         private Integer updateBy;      // THÊM MỚI

         // XÓA: variantName, isActive
     }
