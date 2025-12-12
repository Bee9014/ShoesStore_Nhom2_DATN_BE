 package com.fpl.edu.shoeStore.product.entity;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     import java.time.LocalDate;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class Product {
         private Integer productId;       // INT trong DB
         private Integer categoryId;      // INT trong DB
         private String title;            // Đổi từ "name" → "title"
         private String description;
         private String brand;            // THÊM MỚI
         private String condition;        // THÊM MỚI
         private String defaultImage;     // THÊM MỚI (default_image)
         private String status;           // Đổi từ Boolean isActive → String status
         private LocalDate createAt;      // Đổi từ createdAt, kiểu DATE
         private LocalDate updateAt;      // Đổi từ updatedAt, kiểu DATE
         private Integer createBy;        // THÊM MỚI
         private Integer updateBy;        // THÊM MỚI

         // XÓA: url, productCode, basePrice
     }
