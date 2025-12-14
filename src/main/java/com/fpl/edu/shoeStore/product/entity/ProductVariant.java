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
         private Integer variantId;
         private String productVariantCode;
         private Double price;
         private Integer stockQty;
         private Integer weightGrams;
         private String attribute;
         private String image;
         private LocalDate createAt;
         private LocalDate updateAt;
         private Integer createBy;
         private Integer updateBy;
         private Integer productId;
     }
