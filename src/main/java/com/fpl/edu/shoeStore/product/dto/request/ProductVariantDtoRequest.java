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
         private Integer weightGrams;
         private String attribute;
         private String image;
         private Integer createBy;
         private Integer updateBy;
     }
