package com.fpl.edu.shoeStore.product.dto.request;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class ProductDtoRequest {
         private Integer categoryId;
         private String title;          // Đổi từ name → title
         private String description;
         private String brand;          // THÊM MỚI
         private String condition;      // THÊM MỚI
         private String defaultImage;   // THÊM MỚI
         private String status;         // Đổi từ Boolean isActive → String status
         private Integer createBy;      // THÊM MỚI (cho create/update)
         private Integer updateBy;      // THÊM MỚI

         // XÓA: url, productCode, basePrice
     }