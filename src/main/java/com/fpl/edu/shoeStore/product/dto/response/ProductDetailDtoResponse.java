 package com.fpl.edu.shoeStore.product.dto.response;

     import lombok.AllArgsConstructor;
     import lombok.Builder;
     import lombok.Data;
     import lombok.NoArgsConstructor;

     import java.time.LocalDate;
     import java.util.List;

     @Data
     @NoArgsConstructor
     @AllArgsConstructor
     @Builder
     public class ProductDetailDtoResponse {
         private Integer productId;
         private Integer categoryId;
         private String title;                          // Đổi từ name → title
         private String description;
         private String brand;                          // THÊM MỚI
         private String condition;                      // THÊM MỚI
         private String defaultImage;                   // THÊM MỚI
         private String status;                         // Đổi từ isActive → status
         private LocalDate createAt;                    // Đổi từ createdAt
         private LocalDate updateAt;                    // Đổi từ updatedAt
         private Integer createBy;                      // THÊM MỚI
         private Integer updateBy;                      // THÊM MỚI
         private List<ProductVariantDtoResponse> variants;

         // XÓA: url, productCode, basePrice
     }