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
         private String productVariantCode;
         private Double price;
         private Integer stockQty;
         private Boolean isActive;
         private Integer weightGrams;
         private String size;
         private String color;
         private String image;
         private Integer createBy;
         private Integer updateBy;
     }
